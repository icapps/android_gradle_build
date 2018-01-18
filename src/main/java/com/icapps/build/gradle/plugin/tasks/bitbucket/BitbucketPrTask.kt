package com.icapps.build.gradle.plugin.tasks.bitbucket

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Koen Van Looveren
 */
open class BitbucketPrTask : DefaultTask() {

    var url: String? = null

    @TaskAction
    fun createPr() {
        val token = System.getenv("GRADLE_PLUGIN_BITBUCKET_TOKEN") ?:
                throw IllegalArgumentException("'GRADLE_PLUGIN_BITBUCKET_TOKEN' is not set. Please add your GRADLE_PLUGIN_BITBUCKET_TOKEN to your system.env. To make sure gradle can find your token. Restart your computer.")
    }
}