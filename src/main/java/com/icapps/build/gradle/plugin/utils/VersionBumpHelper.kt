package com.icapps.build.gradle.plugin.utils

import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * @author Koen Van Looveren
 */
object VersionBumpHelper {
    private const val GRADLE_PROPERTIES_FILE = "gradle.properties"

    /**
     * Will update the buildNr of the buildVariant passed as param.
     * The updated buildVariant buildNr will be used to set
     *
     * Format: build{Name}Nr
     *     ex: buildDebugNr
     *     ex: buildBetaNr
     *     ex: buildReleaseNr
     *
     * @param name is the buildVariantName
     */
    fun versionBump(name: String) {
        val input = FileInputStream(GRADLE_PROPERTIES_FILE)
        val prop = PropertiesHelper()
        prop.load(input)

        val buildNr = prop.getProperty("build${name.capitalize()}Nr", "0").toInt() + 1
        prop.setProperty("build${name.capitalize()}Nr", buildNr.toString())
        prop.setProperty("buildNr", buildNr.toString())
        input.close()

        saveProperties(prop)
    }

    fun resetBuildNr() {
        val input = FileInputStream(GRADLE_PROPERTIES_FILE)
        val prop = PropertiesHelper()
        prop.load(input)
        prop.setProperty("buildNr", "1")
        input.close()
        saveProperties(prop)
    }

    private fun saveProperties(properties: PropertiesHelper) {
        val output = FileOutputStream(GRADLE_PROPERTIES_FILE)
        properties.store(output)
        output.close()
    }
}