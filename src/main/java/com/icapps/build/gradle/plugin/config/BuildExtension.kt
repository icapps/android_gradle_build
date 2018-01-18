package com.icapps.build.gradle.plugin.config

import com.chimerapps.gradle.icapps_translations.DownloadTranslationsExtension
import de.felixschulze.gradle.HockeyAppPluginExtension
import de.triplet.gradle.play.PlayPublisherPluginExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Project

/**
 * @author Koen Van Looveren
 */
open class BuildExtension(private val project: Project) {

    var translationConfig: DownloadTranslationsExtension? = null
    var detektConfig: DetektExtension? = null
    var playConfig: PlayPublisherPluginExtension? = null
    var prConfig: PullRequestConfiguration? = PullRequestConfiguration()
    var hockeyConfig: HockeyAppPluginExtension? = null

    open fun playStore(configuration: Action<in PlayPublisherPluginExtension>) {
        playConfig = PlayPublisherPluginExtension().apply { configuration.execute(this) }
    }

    open fun pr(configuration: Action<in PullRequestConfiguration>) {
        prConfig = PullRequestConfiguration().apply { configuration.execute(this) }
    }

    open fun translations(configuration: Action<in DownloadTranslationsExtension>) {
        translationConfig = DownloadTranslationsExtension().apply { configuration.execute(this) }
    }

    open fun detekt(configuration: Action<in DetektExtension>) {
        detektConfig = DetektExtension().apply { configuration.execute(this) }
    }

    open fun hockey(configuration: Action<in HockeyAppPluginExtension>) {
        hockeyConfig = HockeyAppPluginExtension(project).apply { configuration.execute(this) }
    }

}