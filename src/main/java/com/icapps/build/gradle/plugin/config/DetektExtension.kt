package com.icapps.build.gradle.plugin.config

import org.gradle.api.file.ConfigurableFileCollection
import java.io.File

/**
 * @author Koen Van Looveren
 */
open class DetektExtension(var input: ConfigurableFileCollection? = null,
                           var config: ConfigurableFileCollection? = null,
                           var baseline: File? = null,
                           var filters: String? = null,
                           var failFast: Boolean? = null)