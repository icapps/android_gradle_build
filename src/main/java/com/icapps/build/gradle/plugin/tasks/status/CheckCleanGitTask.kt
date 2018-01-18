package com.icapps.build.gradle.plugin.tasks.status

import com.icapps.build.gradle.plugin.utils.GitHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Nicola Verbeeck
 */
open class CheckCleanGitTask : DefaultTask() {

    @TaskAction
    fun checkGitClean() {
        GitHelper.ensureCleanRepo()
    }

}