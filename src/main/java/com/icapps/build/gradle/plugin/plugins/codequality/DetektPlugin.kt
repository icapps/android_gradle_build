package com.icapps.build.gradle.plugin.plugins.codequality

import com.icapps.build.gradle.plugin.Constants
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files

/**
 * @author Koen Van Looveren
 */
class DetektPlugin : BuildSubPlugin {

    override fun init(project: Project) {
        project.plugins.apply(DetektPlugin::class.java)
    }

    override fun configure(project: Project, configuration: BuildExtension) {
        val config = configuration.detektConfig
        if (config == null) {
            project.logger.debug("${Constants.LOGGING_PREFIX} No Detekt block set in gradle. Detekt not be available for this project")
            return
        }
        init(project)

        ensureDetektFile(project)
        ensureBaseLineFile(project)

        project.extensions.getByType(DetektExtension::class.java).apply {

            this.config = config.config ?: getConfigurableFiles(project)
            this.input = config.input ?: guessProgramSources(project)
            baseline = config.baseline ?: getBaseLineFile(project)
            failFast = config.failFast ?: true
        }

        val root = project.rootProject.repositories
        project.buildscript.repositories.addAll(root)
    }

    private fun getConfigurableFiles(project: Project): ConfigurableFileCollection {
        return try {
            @Suppress("DEPRECATION")
            project.layout.configurableFiles(getDefaultConfigFile(project).path)
        } catch (ignored: Throwable) {
            @Suppress("UnstableApiUsage")
            project.objects.fileCollection().from(getDefaultConfigFile(project).path)
        }
    }

    private fun getConfigurableFiles(project: Project, vararg path: String): ConfigurableFileCollection {
        return try {
            @Suppress("DEPRECATION")
            project.layout.configurableFiles(*path)
        } catch (ignored: Throwable) {
            @Suppress("UnstableApiUsage")
            project.objects.fileCollection().from(*path)
        }
    }

    private fun ensureDetektFile(project: Project) {
        val file = getDefaultConfigFile(project)
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                try {
                    Files.createDirectories(file.parentFile.toPath())
                } catch (e: IOException) {
                    project.logger.debug("${Constants.LOGGING_PREFIX} Could not create detekt directory ${e.message}")
                    e.printStackTrace()
                    throw e
                }
            }

            javaClass.getResourceAsStream(DEFAULT_CONFIG_FILE).use { input ->
                FileOutputStream(file).use { output ->
                    try {
                        input.copyTo(output)
                    } catch (e: IOException) {
                        project.logger.debug("${Constants.LOGGING_PREFIX} Could not copy default-detekt.yml to ${file.path} ${e.message}")
                        e.printStackTrace()
                        throw e
                    }
                }
            }
        }
    }

    private fun ensureBaseLineFile(project: Project) {
        val file = getBaseLineFile(project)
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                try {
                    Files.createDirectories(file.parentFile.toPath())
                } catch (e: IOException) {
                    project.logger.debug("${Constants.LOGGING_PREFIX} Could not create detekt directory ${e.message}")
                    e.printStackTrace()
                    throw e
                }
            }

            javaClass.getResourceAsStream(DEFAULT_BASELINE_FILE).use { input ->
                FileOutputStream(file).use { output ->
                    try {
                        input.copyTo(output)
                    } catch (e: IOException) {
                        project.logger.debug("${Constants.LOGGING_PREFIX} Could not copy default-baseline.xml to ${file.path} ${e.message}")
                        e.printStackTrace()
                        throw e
                    }
                }
            }
        }
    }

    private fun guessProgramSources(project: Project): ConfigurableFileCollection {
        val java = project.file("src/main/java")
        val kotlin = project.file("src/main/kotlin")
        if (java.exists() && kotlin.exists())
            return getConfigurableFiles(project, kotlin.absolutePath, java.absolutePath)
        if (java.exists())
            return getConfigurableFiles(project, java.absolutePath)
        if (kotlin.exists())
            return getConfigurableFiles(project, kotlin.absolutePath)
        return getConfigurableFiles(project, project.file("src/main").absolutePath)
    }

    private fun getDefaultConfigFile(project: Project): File {
        return project.rootProject.file(TARGET_DETEKT_FILE)
    }

    private fun getBaseLineFile(project: Project): File {
        return project.rootProject.file(TARGET_BASELINE_FILE)
    }

    private companion object {
        private const val TARGET_DETEKT_FILE = "codecheck/detekt-config.yml"
        private const val TARGET_BASELINE_FILE = "codecheck/detekbaseline.xml"
        private const val DEFAULT_CONFIG_FILE = "/default-detekt.yml"
        private const val DEFAULT_BASELINE_FILE = "/default-baseline.xml"
    }
}