package com.icapps.build.gradle.plugin.tasks.bitbucket

import com.chimerapps.bitbucketcloud.api.Bitbucket
import com.chimerapps.bitbucketcloud.api.model.DefaultReviewer
import com.chimerapps.bitbucketcloud.api.model.Destination
import com.chimerapps.bitbucketcloud.api.model.PullRequest
import com.icapps.build.gradle.plugin.utils.GitHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Koen Van Looveren
 */
open class BitbucketPrTask : DefaultTask() {

    lateinit var user: String
    lateinit var prBranch: String

    private lateinit var bitbucket: Bitbucket

    @TaskAction
    fun createPr() {
        val username = System.getenv("GRADLE_PLUGIN_BITBUCKET_USERNAME_ICAPPS") ?:
                throw IllegalArgumentException("'GRADLE_PLUGIN_BITBUCKET_USERNAME_ICAPPS' is not set. Please add your GRADLE_PLUGIN_BITBUCKET_USERNAME_ICAPPS to your system.env. To make sure gradle can find your username. Restart your computer.")

        val token = System.getenv("GRADLE_PLUGIN_BITBUCKET_TOKEN_ICAPPS") ?:
                throw IllegalArgumentException("'GRADLE_PLUGIN_BITBUCKET_TOKEN_ICAPPS' is not set. Please add your GRADLE_PLUGIN_BITBUCKET_TOKEN_ICAPPS to your system.env. To make sure gradle can find your token. Restart your computer.")

        if (GitHelper.branchNotExists(prBranch)) {
            throw IllegalArgumentException("$prBranch does not exists")
        }

        val currentBranch = GitHelper.getCurrentBranchName()
        val repoSlug = GitHelper.getRepoSlug()

        val messages = GitHelper.getLatestCommitMessages(prBranch)

        val prTitle = if (messages.isEmpty()) {
            "Pull request generated with the Gradle Build Plugin $currentBranch => $prBranch"
        } else {
            messages.last
        }
        val prDescription = messages.joinToString("\n\n")

        bitbucket = Bitbucket(username, token)

        val defaultReviewers = getDefaultReviewers(username, repoSlug)

        val source = Destination(currentBranch)
        val destination = Destination(prBranch)

        val pullRequest = PullRequest(prTitle, prDescription, source, destination, defaultReviewers)

        val responsePr = bitbucket.api
                .postPullRequest(user, repoSlug, pullRequest)
                .execute()

        val errorPr = responsePr.errorBody()
        if (errorPr != null) {
            throw RuntimeException("Create PR Error:\n${errorPr.string()}")
        }
    }

    private fun getDefaultReviewers(username: String, repoSlug: String): List<DefaultReviewer> {
        val responseDefaultReviewers = bitbucket.api
                .getDefaultReviewers(user, repoSlug)
                .execute()

        val errorDefaultReviewers = responseDefaultReviewers.errorBody()
        if (errorDefaultReviewers != null) {
            project.logger.debug("You don't have the admin permission to get the default reviewers")
            return mutableListOf()
        }

        val defaultReviewers = responseDefaultReviewers.body()

        return if (defaultReviewers == null || defaultReviewers.values.isEmpty()) {
            mutableListOf()
        } else {
            defaultReviewers.values.filter { it.username != username }
        }
    }
}