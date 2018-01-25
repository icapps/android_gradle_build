package com.icapps.build.gradle.plugin

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

    private val subPlugins = mutableListOf<BuildSubPlugin>()

    override fun apply(project: Project) {
        val deployToPlayStorePlugin = DeployToPlayStorePlugin()
        subPlugins.replaceAll(TranslationsPlugin(),
                GitStatusPlugin(),
                VersionBumpPlugin(),
                DetektPlugin(),
                PullRequestPlugin(),
                BitBucketPullRequestPlugin(),
                DeployToHockeyPlugin(),
                deployToPlayStorePlugin)

        val extension = project.extensions.create(CONFIG_NAME, BuildExtension::class.java, project)

        project.gradle.startParameter.taskNames.forEach {
            if (it.startsWith("upload") && it.endsWith("ToHockeyApp")) {
                val name = it.removeFirst("upload").removeLast("ToHockeyApp")
                val list = VersionBumpHelper.versionBump(name)
                list.forEach {
                    project.setProperty(it.first, it.second.toString())
                    project.rootProject.setProperty(it.first, it.second.toString())
                }
            }
        }

        project.afterEvaluate {
            subPlugins.forEach { it.configure(project, extension) }
        }
        deployToPlayStorePlugin.init(project)
    }

    companion object {
        private const val CONFIG_NAME = "iCappsBuildConfig"
    }
}

