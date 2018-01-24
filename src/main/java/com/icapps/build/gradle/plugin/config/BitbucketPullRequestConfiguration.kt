package com.icapps.build.gradle.plugin.config

/**
 * @author Koen Van Looveren
 */
open class BitbucketPullRequestConfiguration(open var user: String = "",
                                             open var prBranch: String? = null)