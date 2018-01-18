package com.icapps.build.gradle.plugin.plugins.deploy

import com.google.common.base.Strings
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import de.triplet.gradle.play.PlayPublisherPlugin
import de.triplet.gradle.play.PlayPublisherPluginExtension
import org.gradle.api.Project

/**
 * @author Koen Van Looveren
 */
class DeployToPlayStorePlugin : BuildSubPlugin {

    override fun init(project: Project) {
        project.plugins.apply(PlayPublisherPlugin::class.java)
    }

    override fun configure(project: Project, configuration: BuildExtension) {
        val config = configuration.playConfig
        if (config == null) {
            project.logger.debug("No PlayStore block set in gradle. Play Store Deployment will not be available for this project.")
            return
        }
        init(project)

        if (Strings.isNullOrEmpty(config.serviceAccountEmail)) {
            throw IllegalArgumentException("No ServiceAccountEmail provided in gradle. PlayStore Publish Plugin could not be configured correctly.")
        }

        if (config.jsonFile != null) {
            throw IllegalArgumentException("JsonFile is not safe. User a pk12File instead.")
        }


        if (config.pk12File == null) {
            throw IllegalArgumentException("No pk12File provided in gradle. PlayStore Publish Plugin could not be configured correctly.")
        }

        if (!config.pk12File.exists()) {
            throw IllegalArgumentException("pk12File does not exists. PlayStore Publish Plugin could not be configured correctly.")
        }

        val playStoreConfig = project.extensions.getByType(PlayPublisherPluginExtension::class.java)

        playStoreConfig.serviceAccountEmail = config.serviceAccountEmail
        playStoreConfig.pk12File = config.pk12File
        playStoreConfig.setTrack(config.track as String)
        playStoreConfig.untrackOld = config.untrackOld
        playStoreConfig.userFraction = config.userFraction
    }
}