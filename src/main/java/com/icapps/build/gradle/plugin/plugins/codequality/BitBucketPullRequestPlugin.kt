package com.icapps.build.gradle.plugin.plugins.codequality

import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.tasks.bitbucket.BitbucketPrTask
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
class BitBucketPullRequestPlugin : BuildSubPlugin {

    override fun configure(project: Project, configuration: BuildExtension) {
        val bitbucketConfig = configuration.bitbucketConfig

        if (bitbucketConfig == null) {
            project.logger.debug("No Bitbucket block set in gradle. Bitbucket integration not be available for this project")
            return
        }

        if (bitbucketConfig.url == null)
            throw IllegalArgumentException("No Bitbucket Url set in gradle.")
        else if (bitbucketConfig.projectName == null)
            throw IllegalArgumentException("No Bitbucket ProjectName set in gradle.")

        val openBitbucket = project.tasks.create(OPEN_BITBUCKET, BitbucketPrTask::class.java) {
            it.url = bitbucketConfig.url
            it.dependsOn("pullRequest")
        }
        openBitbucket.group = GROUP_NAME
        openBitbucket.description = "Creates a pull request and will make sure your PR build has run successful."
    }

    private companion object {
        private const val OPEN_BITBUCKET = "createPr"
        private const val GROUP_NAME = "iCapps - Bitbucket"
    }
}