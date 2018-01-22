package com.icapps.build.gradle.plugin.config

/**
 * @author Koen Van Looveren
 */
open class BitbucketPullRequestConfiguration(open var url: String? = null,
                                             open var projectName: String? = null,
                                             open var projectKey: String? = null,
                                             open var repoSlug: String? = null,
                                             open var repoName: String? = null)