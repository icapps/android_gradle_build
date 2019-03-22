package com.icapps.build.gradle.plugin.plugins.deploy

import com.icapps.build.gradle.plugin.Constants
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.plugins.codequality.PullRequestPlugin
import com.icapps.build.gradle.plugin.utils.*
import de.felixschulze.gradle.HockeyAppPlugin
import de.felixschulze.gradle.HockeyAppPluginExtension
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
class DeployToHockeyPlugin : BuildSubPlugin {

    companion object {
        const val RELEASE_NOTES_MAX_LENGTH = 5000
        const val PROPERTY_NOTES = "notes"
        const val ENV_HOCKEY_RELEASE_NOTES = "HOCKEY_RELEASE_NOTES"
        const val ENV_GIT_PREV_SUCCES_COMMIT = "GIT_PREVIOUS_SUCCESSFUL_COMMIT"
    }

    override fun init(project: Project) {
        project.plugins.apply(HockeyAppPlugin::class.java)
        project.tasks.filter { it.group == HockeyAppPlugin.getGROUP_NAME() }
                .forEach {
                    val buildExtension = project.extensions.getByType(BuildExtension::class.java)
                    if (buildExtension.prConfig != null)
                        it.dependsOn(PullRequestPlugin.PULL_REQUEST_TASK)
                    else if (buildExtension.detektConfig != null) {
                        it.dependsOn("detektCheck")
                    }
                    it.doFirst {
                        val hockeyConfig = project.extensions.getByType(HockeyAppPluginExtension::class.java)
                        hockeyConfig.notify = "1"
                        hockeyConfig.notes = getReleaseNotes(project)
                    }

                    it.doLast {
                        val name = it.name.removeFirst("upload").removeLast("ToHockeyApp")
                        val result = VersionBumpHelper.resetBuildNr()
                        project.setProperty(result.first, result.second.toString())
                        project.rootProject.setProperty(result.first, result.second.toString())
                        GitHelper.addAndCommit("Version Bump - ${name.capitalize()}")
                        GitHelper.pushToOrigin()
                    }
                }
    }

    override fun configure(project: Project, configuration: BuildExtension) {
        val config = configuration.hockeyConfig
        if (config == null) {
            project.logger.debug("${Constants.LOG_PREFIX} No Hockey block set in gradle. Hockey App Deployment will not be available for this project.")
            return
        }

        if (config.apiToken == null) {
            throw IllegalArgumentException("No ApiToken provided in gradle. Hockey Plugin could not be configured correctly.")
        }

        init(project)

        val hockeyConfig = project.extensions.getByType(HockeyAppPluginExtension::class.java)

        hockeyConfig.setOutputDirectory(config.outputDirectory)
        hockeyConfig.symbolsDirectory = config.symbolsDirectory
        hockeyConfig.apiToken = config.apiToken
        hockeyConfig.variantToApiToken = config.variantToApiToken
        hockeyConfig.notes = config.notes
        hockeyConfig.variantToNotes = config.variantToNotes
        hockeyConfig.status = config.status
        hockeyConfig.notify = config.notify
        hockeyConfig.variantToNotify = config.variantToNotify
        hockeyConfig.notesType = config.notesType
        hockeyConfig.variantToNotesType = config.variantToNotesType
        hockeyConfig.releaseType = config.releaseType
        hockeyConfig.variantToReleaseType = config.variantToReleaseType
        hockeyConfig.appFileNameRegex = config.appFileNameRegex
        hockeyConfig.mappingFileNameRegex = config.mappingFileNameRegex
        hockeyConfig.commitSha = config.commitSha
        hockeyConfig.buildServerUrl = config.buildServerUrl
        hockeyConfig.repositoryUrl = config.repositoryUrl
        hockeyConfig.tags = config.tags
        hockeyConfig.variantToTags = config.variantToTags
        hockeyConfig.teams = config.teams
        hockeyConfig.users = config.users
        hockeyConfig.timeout = config.timeout
        hockeyConfig.variantToApplicationId = config.variantToApplicationId
        hockeyConfig.teamCityLog = config.teamCityLog
        hockeyConfig.variantToStatus = config.variantToStatus
        hockeyConfig.mandatory = config.mandatory
        hockeyConfig.variantToMandatory = config.variantToMandatory
        hockeyConfig.hockeyApiUrl = config.hockeyApiUrl
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