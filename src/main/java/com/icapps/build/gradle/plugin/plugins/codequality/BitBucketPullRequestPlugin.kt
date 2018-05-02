package com.icapps.build.gradle.plugin.plugins.codequality

import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.tasks.bitbucket.BitbucketPrTask
import org.gradle.api.Project

/**
 * @author Koen Van Looveren
 */
class BitBucketPullRequestPlugin : BuildSubPlugin {

    override fun configure(project: Project, configuration: BuildExtension) {
        val bitbucketConfig = configuration.bitbucketConfig

        if (bitbucketConfig == null) {
            project.logger.debug("No Bitbucket block set in gradle. Bitbucket integration not be available for this project")
            return
        }

        if (bitbucketConfig.user.isNullOrEmpty()) {
            throw IllegalArgumentException("No User provided in gradle (user). Bitbucket integration could not be configured correctly.")
        }
        if (bitbucketConfig.tokenUser.isNullOrEmpty()) {
            bitbucketConfig.tokenUser = bitbucketConfig.user
        }
        if (bitbucketConfig.token.isNullOrEmpty()) {
            throw IllegalArgumentException("No bitbucket app token provided in gradle (token). Bitbucket integration could not be configured correctly.")
        }

        val openBitbucket = project.tasks.create(CREATE_PR_BITBUCKET, BitbucketPrTask::class.java) {
            it.user = bitbucketConfig.user
            it.bitbucketAppKey = bitbucketConfig.token
            it.bitbucketUser = bitbucketConfig.tokenUser!!
            it.prBranch = bitbucketConfig.prBranch ?: "develop"
            it.dependsOn(PullRequestPlugin.PULL_REQUEST_TASK)
        }
        openBitbucket.group = GROUP_NAME
        openBitbucket.description = "Creates a pull request and will make sure your PR build has run successful."
    }

    private companion object {
        private const val CREATE_PR_BITBUCKET = "createPr"
        private const val GROUP_NAME = "iCapps - Bitbucket"
    }
}