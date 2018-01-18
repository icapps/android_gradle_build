package com.icapps.build.gradle.plugin.tasks.status

import com.icapps.build.gradle.plugin.utils.GitHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Nicola Verbeeck
 */
open class CommitTask : DefaultTask() {

    lateinit var message: String

    @TaskAction
    fun commitAndPush() {
        GitHelper.commit(message)
        GitHelper.pushToOrigin()
    }

}