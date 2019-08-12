package com.icapps.build.gradle.plugin

import com.android.build.gradle.AppPlugin
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.plugins.codequality.BitBucketPullRequestPlugin
import com.icapps.build.gradle.plugin.plugins.codequality.DetektPlugin
import com.icapps.build.gradle.plugin.plugins.codequality.PullRequestPlugin
import com.icapps.build.gradle.plugin.plugins.deploy.DeployToAppCenterPlugin
import com.icapps.build.gradle.plugin.plugins.deploy.DeployToPlayStorePlugin
import com.icapps.build.gradle.plugin.plugins.status.GitStatusPlugin
import com.icapps.build.gradle.plugin.plugins.translations.TranslationsPlugin
import com.icapps.build.gradle.plugin.utils.replaceAll
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Koen Van Looveren
 */
open class BuildPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            initForAndroidLibrary(project)
            project.logger.debug("${Constants.LOGGING_PREFIX} This plugin is made for Android Application Projects. The Android Plugin needs to be applied before this plugin.")
            return
        }
        initForAndroidApplications(project)
    }

    private fun initForAndroidLibrary(project: Project) {
        val extension = project.extensions.create(CONFIG_NAME, BuildExtension::class.java, project)
        val subPlugins = mutableListOf<BuildSubPlugin>()

        subPlugins.replaceAll(DetektPlugin())
        project.afterEvaluate {
            subPlugins.forEach { it.configure(project, extension) }
        }
    }

    private fun initForAndroidApplications(project: Project) {
        val extension = project.extensions.create(CONFIG_NAME, BuildExtension::class.java, project)
        val subPlugins = mutableListOf<BuildSubPlugin>()

        val deployToPlayStorePlugin = DeployToPlayStorePlugin()
        val deployToAppCenterPlugin = DeployToAppCenterPlugin()
        val translations = TranslationsPlugin()
        subPlugins.replaceAll(translations,
                GitStatusPlugin(),
                DetektPlugin(),
                PullRequestPlugin(),
                BitBucketPullRequestPlugin(),
                deployToAppCenterPlugin,
                deployToPlayStorePlugin)

        project.afterEvaluate {
            subPlugins.forEach { it.configure(project, extension) }
        }
        translations.init(project)
        deployToAppCenterPlugin.init(project)
        deployToPlayStorePlugin.init(project)
    }

    companion object {
        private const val CONFIG_NAME = "iCappsBuildConfig"
    }
}

