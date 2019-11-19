package com.icapps.build.gradle.plugin.config

import com.chimerapps.gradle.AppCenterExtension
import com.chimerapps.gradle.icapps_translations.DownloadTranslationsExtension
import org.gradle.api.Action
import org.gradle.api.Project

/**
 * @author Koen Van Looveren
 */
open class BuildExtension(private val project: Project) {

    var translationConfig: DownloadTranslationsExtension? = null
    var detektConfig: DetektExtension? = null
    var prConfig: PullRequestConfiguration? = PullRequestConfiguration()
    var bitbucketConfig: BitbucketPullRequestConfiguration? = null
    var appCenterExtension: AppCenterExtension? = null

    open fun pr(configuration: Action<in PullRequestConfiguration>) {
        prConfig = PullRequestConfiguration().apply { configuration.execute(this) }
    }

    open fun bitbucket(configuration: Action<in BitbucketPullRequestConfiguration>) {
        bitbucketConfig = BitbucketPullRequestConfiguration().apply { configuration.execute(this) }
    }

    open fun translations(configuration: Action<in DownloadTranslationsExtension>) {
        translationConfig = DownloadTranslationsExtension().apply { configuration.execute(this) }
    }

    open fun detekt(configuration: Action<in DetektExtension>) {
        detektConfig = DetektExtension().apply { configuration.execute(this) }
    }

    open fun appCenter(configuration: Action<in AppCenterExtension>) {
        appCenterExtension = AppCenterExtension().apply { configuration.execute(this) }
    }
}