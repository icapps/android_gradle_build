package com.icapps.build.gradle.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.plugins.codequality.BitBucketPullRequestPlugin
import com.icapps.build.gradle.plugin.plugins.codequality.DetektPlugin
import com.icapps.build.gradle.plugin.plugins.codequality.PullRequestPlugin
import com.icapps.build.gradle.plugin.plugins.deploy.DeployToHockeyPlugin
import com.icapps.build.gradle.plugin.plugins.deploy.DeployToPlayStorePlugin
import com.icapps.build.gradle.plugin.plugins.status.GitStatusPlugin
import com.icapps.build.gradle.plugin.plugins.translations.TranslationsPlugin
import com.icapps.build.gradle.plugin.plugins.versionbump.VersionBumpPlugin
import com.icapps.build.gradle.plugin.utils.VersionBumpHelper
import com.icapps.build.gradle.plugin.utils.removeFirst
import com.icapps.build.gradle.plugin.utils.removeLast
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
        val translations = TranslationsPlugin()
        subPlugins.replaceAll(translations,
                GitStatusPlugin(),
                VersionBumpPlugin(),
                DetektPlugin(),
                PullRequestPlugin(),
                BitBucketPullRequestPlugin(),
                DeployToHockeyPlugin(),
                deployToPlayStorePlugin)

        var versionBump = false
        project.gradle.startParameter.taskNames.forEach {
            if (it.startsWith("upload") && it.endsWith("ToHockeyApp")) {
                val name = it.removeFirst("upload").removeLast("ToHockeyApp")
                val list = if (name.isNotEmpty()) {
                    VersionBumpHelper.versionBump(name)
                } else {
                    if (!project.hasProperty("buildNrName")) {
                        throw RuntimeException("$it need to specify the 'branchNrName' as a param")
                    }
                    VersionBumpHelper.versionBump()
                }
                list.forEach {
                    project.setProperty(it.first, it.second.toString())
                    project.rootProject.setProperty(it.first, it.second.toString())
                }

                if (project.hasProperty("buildNrName") && !versionBump) {
                    versionBump = true
                    val bump = VersionBumpHelper.versionBump(project.property("buildNrName").toString())

                    bump.forEach {
                        project.setProperty(it.first, it.second.toString())
                        project.rootProject.setProperty(it.first, it.second.toString())
                    }
                }
            }
        }

        VersionBumpHelper.init()

        project.afterEvaluate {
            if (project.hasProperty("buildNrName")) {
                VersionBumpHelper.init(project.property("buildNrName").toString())
            }
            val variants = project.extensions.findByType(AppExtension::class.java).applicationVariants
            VersionBumpHelper.init(variants.map { it.name })

            subPlugins.forEach { it.configure(project, extension) }
        }
        translations.init(project)
        deployToPlayStorePlugin.init(project)
    }

    companion object {
        private const val CONFIG_NAME = "iCappsBuildConfig"
    }
}

