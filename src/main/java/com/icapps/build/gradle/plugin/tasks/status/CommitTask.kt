package com.icapps.build.gradle.plugin.tasks.status

import com.icapps.build.gradle.plugin.utils.GitHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * @author Nicola Verbeeck
 */
open class CommitTask : DefaultTask() {

    @get:Internal
    lateinit var message: String

    @TaskAction
    fun commitAndPush() {
        GitHelper.addAndCommit(message)
        GitHelper.pushToOrigin()
    }

}