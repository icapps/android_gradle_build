package com.icapps.build.gradle.plugin.plugins.codequality

import com.android.build.gradle.AppExtension
import com.icapps.build.gradle.plugin.Constants
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.config.PullRequestConfiguration
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.utils.GitHelper
import com.icapps.build.gradle.plugin.utils.exists
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Nicola Verbeeck
 */
class PullRequestPlugin : BuildSubPlugin {

    override fun configure(project: Project, configuration: BuildExtension) {
        val prConfig = configuration.prConfig

        if (prConfig == null) {
            project.logger.debug("${Constants.LOGGING_PREFIX} No Pr block set in gradle. Pr not be available for this project")
            return
        }

        val task = project.tasks.create(PULL_REQUEST_TASK) { task ->
            if (prConfig.detekt && configuration.detektConfig != null)
                task.dependsOn("detekt")
            if (prConfig.lint)
                addLintTask(project, task, prConfig)
            if (prConfig.unitTest)
                addUnitTestTask(project, task, prConfig)
            if (prConfig.deviceTest)
                addDeviceTestTask(project, task, prConfig)
        }
        task.doFirst {
            GitHelper.ensureCleanRepo()
        }
        task.group = "verification"
        task.description = "Pull request builds will be triggered with this command."
    }

    private fun addLintTask(project: Project, task: Task, prConfig: PullRequestConfiguration) {
        val variant = if (prConfig.lintVariant.isNotBlank())
            prConfig.lintVariant
        else
            guessName(project, "lintVariant")

        task.dependsOn("lint${variant.capitalize()}")
    }

    private fun addUnitTestTask(project: Project, task: Task, prConfig: PullRequestConfiguration) {
        val variant = if (prConfig.unitTestVariant.isNotBlank())
            prConfig.unitTestVariant
        else
            guessName(project, "unitTestVariant")

        task.dependsOn("test${variant.capitalize()}UnitTest")
    }

    private fun addDeviceTestTask(project: Project, task: Task, prConfig: PullRequestConfiguration) {
        val variant = if (prConfig.deviceTestVariant.isNotBlank())
            prConfig.deviceTestVariant
        else
            guessName(project, "deviceTestVariant")

        task.dependsOn("connected${variant.capitalize()}AndroidTest")
    }

    private fun guessName(project: Project, debugName: String): String {
        val androidExtension = project.extensions.getByType(AppExtension::class.java)

        if (androidExtension.applicationVariants.size == androidExtension.buildTypes.size) {
            if (androidExtension.buildTypes.exists { it.name == "release" }) {
                return "release"
            }
            val notDebuggable = androidExtension.buildTypes.find { !it.isDebuggable }
            if (notDebuggable != null)
                return notDebuggable.name
            throw IllegalStateException("No non-debuggable variants found to run as part of PR. Specify it manually using '$debugName'")
        }

        throw IllegalStateException("Cannot determine variant for part of PR, specify it manually using '$debugName'")
    }

    companion object {
        const val PULL_REQUEST_TASK = "pullRequest"
    }
}