package com.icapps.build.gradle.plugin.plugins.status

import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.tasks.status.CheckCleanRepoTask
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
class GitStatusPlugin : BuildSubPlugin {

    override fun configure(project: Project, configuration: BuildExtension) {
        val task = project.tasks.create(CLEAN_GIT_TASK, CheckCleanRepoTask::class.java)
        task.description = "Will check if your git is clean. Otherwise an exception is thrown."
    }

    companion object {
        const val CLEAN_GIT_TASK = "ensureCleanRepo"
    }

}