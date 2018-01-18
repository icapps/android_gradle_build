package com.icapps.build.gradle.plugin.plugins

import com.icapps.build.gradle.plugin.config.BuildExtension
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
interface BuildSubPlugin {

    fun init(project: Project) {}

    fun configure(project: Project, configuration: BuildExtension)

}