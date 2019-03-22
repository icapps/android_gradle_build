package com.icapps.build.gradle.plugin.plugins.translations

import com.chimerapps.gradle.icapps_translations.DownloadTranslationsExtension
import com.chimerapps.gradle.icapps_translations.DownloadTranslationsPlugin
import com.icapps.build.gradle.plugin.Constants
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
class TranslationsPlugin : BuildSubPlugin {

    override fun init(project: Project) {
        project.plugins.apply(DownloadTranslationsPlugin::class.java)
    }

    override fun configure(project: Project, configuration: BuildExtension) {
        val config = configuration.translationConfig
        if (config == null) {
            project.logger.debug("${Constants.LOG_PREFIX} No translations block set in gradle. Translations will not be available for this project")
            return
        }

        if (config.apiKey == null) {
            throw IllegalArgumentException("No ApiKey provided in gradle. iCapps Translations could not be configured correctly.")
        }
        init(project)
        val translationConfig = project.extensions.getByType(DownloadTranslationsExtension::class.java)
        //translationConfig.configurations.addAll(config.configurations)

        translationConfig.apiKey = config.apiKey
        translationConfig.fileName = config.fileName
        translationConfig.sourceRoot = config.sourceRoot
        translationConfig.fileType = config.fileType

        translationConfig.sourceRootProvider = config.sourceRootProvider
        translationConfig.languageRename = config.languageRename
        translationConfig.fileNameProvider = config.fileNameProvider
        translationConfig.folderProvider = config.folderProvider
        translationConfig.languageFilter = config.languageFilter
    }
}