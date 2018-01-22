package com.icapps.build.gradle.plugin.plugins.deploy

import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import de.felixschulze.gradle.HockeyAppPlugin
import de.felixschulze.gradle.HockeyAppPluginExtension
import de.felixschulze.gradle.HockeyAppUploadTask
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
class DeployToHockeyPlugin : BuildSubPlugin {

    override fun init(project: Project) {
        project.plugins.apply(HockeyAppPlugin::class.java)
    }

    override fun configure(project: Project, configuration: BuildExtension) {
        val config = configuration.hockeyConfig
        if (config == null) {
            project.logger.debug("No Hockey block set in gradle. Hockey App Deployment will not be available for this project.")
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
        hockeyConfig.notes = config.notes ?: "No release notes given."
        hockeyConfig.variantToNotes = config.variantToNotes
        hockeyConfig.status = config.status ?: "2"
        hockeyConfig.notify = config.notify ?: "1"
        hockeyConfig.variantToNotify = config.variantToNotify
        hockeyConfig.notesType = config.notesType ?: "1"
        hockeyConfig.variantToNotesType = config.variantToNotesType
        hockeyConfig.releaseType = config.releaseType ?: "0"
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

}