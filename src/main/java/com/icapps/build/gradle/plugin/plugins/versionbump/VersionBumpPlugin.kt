package com.icapps.build.gradle.plugin.plugins.versionbump

import com.android.build.gradle.AppExtension
import com.icapps.build.gradle.plugin.config.BuildExtension
import com.icapps.build.gradle.plugin.plugins.BuildSubPlugin
import com.icapps.build.gradle.plugin.tasks.status.CheckCleanRepoTask
import com.icapps.build.gradle.plugin.tasks.status.CommitTask
import com.icapps.build.gradle.plugin.tasks.versionbump.VersionBumpTask
import org.gradle.api.Project

/**
 * @author Nicola Verbeeck
 */
class VersionBumpPlugin : BuildSubPlugin {

    override fun configure(project: Project, configuration: BuildExtension) {
        val tasks = mutableMapOf<String, MutableList<VersionBumpTask>>()
        val justTasks = mutableMapOf<String, MutableList<VersionBumpTask>>()

        val androidExtension = project.extensions.getByType(AppExtension::class.java)
        var count = 0

        androidExtension.applicationVariants.forEach { variant ->
            ++count
            val versionBumpTask = project.tasks.create("versionBump${variant.name.capitalize()}", VersionBumpTask::class.java) {
                it.flavorName = variant.name
            }
            val justVersionBumpTask = project.tasks.create("justVersionBump${variant.name.capitalize()}", VersionBumpTask::class.java) {
                it.flavorName = variant.name
                it.commit = false
            }
            val key = variant.buildType.name

            versionBumpTask.group = GROUP_NAME
            versionBumpTask.description = "A version bump will be executed of ${key.capitalize()}. Changes will be committed."

            justVersionBumpTask.group = GROUP_NAME
            justVersionBumpTask.description = "A version bump will be executed of ${key.capitalize()}. Changes will not be committed."

            tasks.getOrPut(key, { mutableListOf() }).add(versionBumpTask)
            justTasks.getOrPut(key, { mutableListOf() }).add(justVersionBumpTask)
        }

        if (count == androidExtension.buildTypes.size)
            return

        //There is need to add extra tasks that aggregate the version bumps over build types. Implement them as deps!

        androidExtension.buildTypes.forEach { buildType ->
            /* Version bump */
            val aggregate = project.tasks.create("versionBump${buildType.name.capitalize()}") { task ->

                task.doFirst {
                    CheckCleanRepoTask().apply {  }.checkCleanRepo()

                    tasks[buildType.name]?.forEach {
                        it.versionBump()
                    }
                    CommitTask().apply { message = "Version bump for ${buildType.name}" }.commitAndPush()
                }
            }
            aggregate.group = GROUP_NAME
            aggregate.description = "A version bump will be executed of ${buildType.name.capitalize()}. Changes will be committed."

            /* Just Version bump */
            val justAgregate = project.tasks.create("justVersionBump${buildType.name.capitalize()}") { task ->
                task.doFirst {
                    justTasks[buildType.name]?.forEach {
                        it.versionBump()
                    }
                }
            }
            justAgregate.group = GROUP_NAME
            justAgregate.description = "A version bump will be executed of ${buildType.name.capitalize()}. Changes will not be committed."
        }
    }

    companion object {
        const val GROUP_NAME = "iCapps - Version bump"
    }

}