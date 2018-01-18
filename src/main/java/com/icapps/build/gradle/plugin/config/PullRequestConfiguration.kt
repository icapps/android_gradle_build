package com.icapps.build.gradle.plugin.config

/**
 * @author Nicola Verbeeck
 */
open class PullRequestConfiguration(open var lint: Boolean = true,
                                    open var detekt: Boolean = true,
                                    open var unitTest: Boolean = true,
                                    open var deviceTest: Boolean = false) {

    open var lintVariant: String = ""
    open var unitTestVariant: String = ""
    open var deviceTestVariant: String = ""

}