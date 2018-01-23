package com.icapps.build.gradle.plugin.tasks.bitbucket

import com.icapps.build.gradle.plugin.utils.GitHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Koen Van Looveren
 */
open class BitbucketPrTask : DefaultTask() {

    lateinit var url: String
    lateinit var projectName: String
    lateinit var repoName: String
    lateinit var repoSlug: String
    lateinit var projectKey: String
    lateinit var reviewerList: List<String>

    @TaskAction
    fun createPr() {
        val token = System.getenv("GRADLE_PLUGIN_BITBUCKET_TOKEN") ?:
                throw IllegalArgumentException("'GRADLE_PLUGIN_BITBUCKET_TOKEN' is not set. Please add your GRADLE_PLUGIN_BITBUCKET_TOKEN to your system.env. To make sure gradle can find your token. Restart your computer.")

        if (GitHelper.branchNotExists(branchName)) {
            throw IllegalArgumentException("$branchName does not exists")
        }

        val currentBranch = GitHelper.getCurrentBranchName()

        val prTitle = "Test From Gradle Plugin" // eerst commit die afwijkt van branch waar ge in wilt mergen
        val prDescription = "Description From Gradle Pugin" // alle commits

        val fromId = branchPrefix + currentBranch
        val toId = branchPrefix + branchName
    }

    companion object {
        val branchName: String = "develop"
        val branchPrefix: String = "refs/heads/"
    }
}