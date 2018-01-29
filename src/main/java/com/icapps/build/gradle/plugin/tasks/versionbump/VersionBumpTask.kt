package com.icapps.build.gradle.plugin.tasks.versionbump

import com.icapps.build.gradle.plugin.utils.GitHelper
import com.icapps.build.gradle.plugin.utils.VersionBumpHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Koen Van Looveren
 */
open class VersionBumpTask : DefaultTask() {

    lateinit var flavorName: String
    var commit: Boolean = true

    @TaskAction
    fun versionBump() {
        VersionBumpHelper.versionBump(flavorName)
        val list = if (flavorName.isNotEmpty()) {
            VersionBumpHelper.versionBump(flavorName)
        } else {
            VersionBumpHelper.versionBump()
        }
        list.forEach {
            project.setProperty(it.first, it.second.toString())
            project.rootProject.setProperty(it.first, it.second.toString())
        }
        if (commit) {
            GitHelper.addAndCommit("Version Bump - ${flavorName.capitalize()}")
            GitHelper.pushToOrigin()
        }
    }
}