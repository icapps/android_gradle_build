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
        if (commit) {
            GitHelper.commit("Version Bump - ${flavorName.capitalize()}", "gradle.properties")
            GitHelper.pushToOrigin()
        }
    }
}