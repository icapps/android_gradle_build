package com.icapps.build.gradle.plugin.tasks.bitbucket

import com.icapps.build.gradle.plugin.utils.GitHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.stream.Collectors

/**
 * @author Koen Van Looveren
 */
open class BitbucketPrTask : DefaultTask() {

    lateinit var user: String

    //private lateinit var bitbucket: Bitbucket

    @TaskAction
    fun createPr() {
        val username = System.getenv("GRADLE_PLUGIN_BITBUCKET_USERNAME_ICAPPS") ?:
                throw IllegalArgumentException("'GRADLE_PLUGIN_BITBUCKET_USERNAME_ICAPPS' is not set. Please add your GRADLE_PLUGIN_BITBUCKET_USERNAME_ICAPPS to your system.env. To make sure gradle can find your username. Restart your computer.")

        val token = System.getenv("GRADLE_PLUGIN_BITBUCKET_TOKEN_ICAPPS") ?:
                throw IllegalArgumentException("'GRADLE_PLUGIN_BITBUCKET_TOKEN_ICAPPS' is not set. Please add your GRADLE_PLUGIN_BITBUCKET_TOKEN_ICAPPS to your system.env. To make sure gradle can find your token. Restart your computer.")

        if (GitHelper.branchNotExists(branchName)) {
            throw IllegalArgumentException("$branchName does not exists")
        }

        val currentBranch = GitHelper.getCurrentBranchName()
        val repoSlug = GitHelper.getRepoSlug()

        val messages = GitHelper.getLatestCommitMessages(branchName)

        val prTitle = if (messages.isEmpty()) {
            "Pull request generated with the Gradle Build Plugin $currentBranch => $branchName"
        } else {
            messages.first
        }
        val prDescription = "Pull request generated with the Gradle Build Plugin $currentBranch => $branchName\n\n" +
                messages.joinToString("\n")
        /*
        bitbucket = Bitbucket(username, token)

        val defaultReviewers = getDefaultReviewers(username, repoSlug)

        val source = Destination(currentBranch)
        val destination = Destination(branchName)

        val pullRequest = PullRequest(prTitle, prDescription, source, destination, defaultReviewers)

        val responsePr = bitbucket.getApi()
                .postPullRequest(user, repoSlug, pullRequest)
                .execute()
        val errorPr = responsePr.errorBody()
        if (errorPr != null) {
            throw RuntimeException("Create PR Error:\n${errorPr.string()}")
        }
        */
    }

    /*
        private fun getDefaultReviewers(username: String, repoSlug: String): List<DefaultReviewer> {
            val responseDefaultReviewers = bitbucket.getApi()
                    .getDefaultReviewers(user, repoSlug)
                    .execute()

            val errorDefaultReviewers = responseDefaultReviewers.errorBody()
            if (errorDefaultReviewers != null) {
                throw RuntimeException("Default Reviewers Error:\n${errorDefaultReviewers.string()}")
            }

            val defaultReviewers = responseDefaultReviewers.body()
            return if (defaultReviewers == null || defaultReviewers.isEmpty()) {
                mutableListOf<DefaultReviewer>()
            } else {
                defaultReviewers!!.getValues().filter { it.getUsername() != username }
            }
        }
    */
    companion object {
        val branchName: String = "develop"
    }
}