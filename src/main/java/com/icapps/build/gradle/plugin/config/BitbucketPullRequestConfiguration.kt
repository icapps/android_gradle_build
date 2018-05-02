package com.icapps.build.gradle.plugin.config

/**
 * @author Koen Van Looveren
 */
open class BitbucketPullRequestConfiguration(open var user: String = "",
                                             open var prBranch: String? = null,
                                             open var tokenUser: String? = null,
                                             open var token: String = "")