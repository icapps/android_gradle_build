package com.icapps.build.gradle.plugin.plugins.deploy

import com.chimerapps.gradle.AndroidGradleAppCenterPlugin
import com.chimerapps.gradle.AppCenterExtension
import com.icapps.build.gradle.plugin.Constants
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.utils.*
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
class DeployToAppCenterPlugin : BuildSubPlugin {

    companion object {
        const val GROUP_APPCENTER_PLUGIN = "AppCenter"
        const val RELEASE_NOTES_MAX_LENGTH = 5000
        const val PROPERTY_NOTES = "notes"
        const val ENV_HOCKEY_RELEASE_NOTES = "HOCKEY_RELEASE_NOTES"
        const val ENV_GIT_PREV_SUCCES_COMMIT = "GIT_PREVIOUS_SUCCESSFUL_COMMIT"
    }

    override fun init(project: Project) {
        project.plugins.apply(AndroidGradleAppCenterPlugin::class.java)
        project.tasks.filter { it.group == GROUP_APPCENTER_PLUGIN }
                .forEach {
                    it.doFirst {
                        val appCenter = project.extensions.getByType(AppCenterExtension::class.java)
                        appCenter.releaseNotes = getReleaseNotes(project)
                    }
                }
    }

    override fun configure(project: Project, configuration: BuildExtension) {
        val config = configuration.appCenterExtension
        if (config == null) {
            project.logger.debug("${Constants.LOGGING_PREFIX} No AppCenter block set in gradle. AppCenter App Deployment will not be available for this project.")
            return
        }

        if (config.apiKey == null) {
            throw IllegalArgumentException("No ApiKey provided in gradle. AppCenter Plugin could not be configured correctly.")
        }

        if (config.appOwner == null) {
            throw IllegalArgumentException("No AppOwner provided in gradle. AppCenter Plugin could not be configured correctly.")
        }

        init(project)

        val hockeyConfig = project.extensions.getByType(AppCenterExtension::class.java)
        hockeyConfig.apply {
            apiKey = config.apiKey
            appOwner = config.appOwner
            applicationIdToAppName = config.applicationIdToAppName
            notifyTesters = config.notifyTesters
            testers = config.testers
            releaseNotes = config.releaseNotes
            variantToAppName = config.variantToAppName
            flavorToAppName = config.flavorToAppName
        }
    }

    private fun getReleaseNotes(project: Project): String {
        val fromEnv: String? = System.getenv(ENV_HOCKEY_RELEASE_NOTES)
        val lastSuccess: String? = System.getenv(ENV_GIT_PREV_SUCCES_COMMIT)
        val notes: String = when {
            project.hasProperty(PROPERTY_NOTES) -> project.property(PROPERTY_NOTES).toString()
            isNotNullOrEmpty(fromEnv) -> fromEnv ?: "No release notes were given."
            else -> {
                val reader = if (isNotNullOrEmpty(lastSuccess)) {
                    ShellHelper.execWithReader(listOf("git", "log", "$lastSuccess..HEAD", "--pretty=format:%s", "--no-merges"))
                } else {
                    ShellHelper.execWithReader(listOf("git", "log", "--all", "--pretty=format:%s", "--no-merges"))
                }
                val stringBuilder = StringBuilder()
                reader.readLines()
                        .filter { it.isNotEmpty() }
                        .forEach { stringBuilder.append("* ").append(it).append("\n") }
                stringBuilder.toString()
            }
        }
        return notes.substring(0, Math.min(notes.length, RELEASE_NOTES_MAX_LENGTH))
    }
}