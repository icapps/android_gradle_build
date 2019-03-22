package com.icapps.build.gradle.plugin.plugins.codequality

import com.icapps.build.gradle.plugin.Constants
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.extensions.ProfileExtension
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files

/**
 * @author Nicola Verbeeck
 */
class DetektPlugin : BuildSubPlugin {

    override fun init(project: Project) {
        project.plugins.apply(DetektPlugin::class.java)
    }

    override fun configure(project: Project, configuration: BuildExtension) {
        val config = configuration.detektConfig
        if (config == null) {
            project.logger.debug("${Constants.LOG_PREFIX} No Detekt block set in gradle. Detekt not be available for this project")
            return
        }
        init(project)

        ensureDetektFile(project)

        val detektConfig = project.extensions.getByType(DetektExtension::class.java)

        detektConfig.version = config.version
        detektConfig.debug = config.debug
        detektConfig.ideaExtension = transformIdea(project, config.ideaExtension)
        detektConfig.profile = config.profile

        injectProfile(project, detektConfig, config.systemOrDefaultProfile() ?: createDefaultProfile(project))

        val root = project.rootProject.repositories
        project.buildscript.repositories.addAll(root)
    }

    private fun createDefaultProfile(project: Project): ProfileExtension {
        return ProfileExtension("main").apply {
            input = project.file("src/main/java").absolutePath
            config = project.rootProject.file(TARGET_FILE).absolutePath
            filters = ".*test.*,.*/resources/.*,.*/tmp/.*"
            output = project.file("reports").absolutePath
            outputName = "detektReport"
            parallel = true
        }
    }

    private fun injectProfile(project: Project, config: DetektExtension, source: ProfileExtension) {
        config.profile(source.name, Action { target ->

            target.input = source.input ?: guessProgramSources(project)
            target.config = source.config ?: project.rootProject.file(TARGET_FILE).absolutePath
            target.configResource = source.configResource
            target.filters = source.filters ?: ".*test.*,.*/resources/.*,.*/tmp/.*"
            target.ruleSets = source.ruleSets
            target.output = source.output ?: project.file("reports").absolutePath
            target.outputName = source.outputName ?: "detektReport"
            target.baseline = source.baseline
            target.parallel = source.parallel
            target.disableDefaultRuleSets = source.disableDefaultRuleSets
            target.plugins = source.plugins
        })
    }

    private fun transformIdea(project: Project, ideaExtension: IdeaExtension): IdeaExtension {
        return ideaExtension.apply {
            path = path ?: ""
            report = report ?: project.file("reports/report.xml").absolutePath
            inspectionsProfile = inspectionsProfile ?: ""
        }
    }

    private fun ensureDetektFile(project: Project) {
        val file = project.rootProject.file(TARGET_FILE)
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                try {
                    Files.createDirectories(file.parentFile.toPath())
                } catch (e: IOException) {
                    project.logger.debug("${Constants.LOG_PREFIX} Could not create detekt directory ${e.message}")
                    e.printStackTrace()
                    throw e
                }
            }

            javaClass.getResourceAsStream("/default-detekt.yml").use { input ->
                FileOutputStream(file).use { output ->
                    try {
                        input.copyTo(output)
                    } catch (e: IOException) {
                        project.logger.debug("${Constants.LOG_PREFIX} Could not copy default-detekt.yml to ${file.path} ${e.message}")
                        e.printStackTrace()
                        throw e
                    }
                }
            }
        }
    }

    private fun guessProgramSources(project: Project): String {
        val java = project.file("src/main/java")
        val kotlin = project.file("src/main/kotlin")
        if (!java.exists())
            return kotlin.absolutePath
        if (!kotlin.exists())
            return java.absolutePath

        return project.file("src/main").absolutePath
    }

    private companion object {
        private const val TARGET_FILE = "codecheck/detekt-config.yml"
    }

}