package com.icapps.build.gradle.plugin.tasks.bitbucket

import com.chimerapps.bitbucketcloud.api.Bitbucket
import com.chimerapps.bitbucketcloud.api.model.DefaultReviewer
import com.chimerapps.bitbucketcloud.api.model.Destination
import com.chimerapps.bitbucketcloud.api.model.PullRequest
import com.icapps.build.gradle.plugin.Constants
import com.icapps.build.gradle.plugin.utils.GitHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * @author Koen Van Looveren
 */
open class BitbucketPrTask : DefaultTask() {

    @field:Internal
    @set:Internal
    @get:Internal
    lateinit var user: String
    @field:Internal
    @set:Internal
    @get:Internal
    lateinit var bitbucketUser: String
    @field:Internal
    @set:Internal
    @get:Internal
    lateinit var bitbucketAppKey: String
    @field:Internal
    @set:Internal
    @get:Internal
    lateinit var prBranch: String

    @field:Internal
    private lateinit var bitbucket: Bitbucket

    @TaskAction
    fun createPr() {
        if (GitHelper.branchNotExists(prBranch)) {
            throw IllegalArgumentException("$prBranch does not exists")
        }

        val currentBranch = GitHelper.getCurrentBranchName()
        val repoSlug = GitHelper.getRepoSlug()

        val messages = GitHelper.getLatestCommitMessages(prBranch)

        val prTitle = currentBranch

        val prDescription = messages.joinToString("\n\n")

        bitbucket = Bitbucket(bitbucketUser, bitbucketAppKey)

        val defaultReviewers = getDefaultReviewers(bitbucketUser, repoSlug)

        val source = Destination(currentBranch)
        val destination = Destination(prBranch)

        val pullRequest = PullRequest(0, prTitle, prDescription, source, destination, defaultReviewers)

        val responsePr = bitbucket.api
                .postPullRequest(user, repoSlug, pullRequest)
                .execute()

        val errorPr = responsePr.errorBody()
        if (errorPr != null) {
            throw RuntimeException("Create PR Error:\n${errorPr.string()}")
        }

        val response = responsePr.body() ?: return
        println("Your pull request has been made. You can check the details in the link below.")
        println("https://bitbucket.org/$user/$repoSlug/pull-requests/${response.id}")
    }

    private fun getDefaultReviewers(username: String, repoSlug: String): List<DefaultReviewer> {
        val responseDefaultReviewers = bitbucket.api
                .getDefaultReviewers(user, repoSlug)
                .execute()

        val errorDefaultReviewers = responseDefaultReviewers.errorBody()
        if (errorDefaultReviewers != null) {
            project.logger.debug("${Constants.LOGGING_PREFIX} You don't have the admin permission to get the default reviewers")
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