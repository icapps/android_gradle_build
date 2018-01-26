package com.icapps.build.gradle.plugin.utils

import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * @author Koen Van Looveren
 */
object VersionBumpHelper {
    private const val GRADLE_PROPERTIES_FILE = "gradle.properties"

    private const val buildNrKey = "buildNr"

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
    fun versionBump(name: String): List<Pair<String, Int>> {
        val input = FileInputStream(GRADLE_PROPERTIES_FILE)
        val prop = PropertiesHelper()
        prop.load(input)

        val specificBuildNrKey = "build${name.capitalize()}Nr"

        val buildNr = prop.getProperty(specificBuildNrKey, "0").toInt() + 1
        prop.setProperty(specificBuildNrKey, buildNr.toString())
        prop.setProperty(buildNrKey, buildNr.toString())
        input.close()
        saveProperties(prop)

        val list = mutableListOf<Pair<String, Int>>()
        list.add(Pair(specificBuildNrKey, buildNr))
        list.add(Pair(buildNrKey, buildNr))
        return list
    }

    fun versionBump(): List<Pair<String, Int>> {
        val input = FileInputStream(GRADLE_PROPERTIES_FILE)
        val prop = PropertiesHelper()
        prop.load(input)
        val list = mutableListOf<Pair<String, Int>>()
        prop.filter { it.key.toString().startsWith("build") && it.key.toString().endsWith("Nr") }
                .forEach {
                    val buildNr = it.value.toString().toInt() + 1
                    prop.setProperty(it.key.toString(), buildNr.toString())
                    list.add(Pair(it.key.toString(), buildNr))
                }
        input.close()
        prop.setProperty(buildNrKey, "1")
        saveProperties(prop)

        list.add(Pair(buildNrKey, 1))
        return list
    }

    fun resetBuildNr(): Pair<String, Int> {
        val input = FileInputStream(GRADLE_PROPERTIES_FILE)
        val prop = PropertiesHelper()
        prop.load(input)
        prop.setProperty(buildNrKey, "1")
        input.close()
        saveProperties(prop)
        return Pair(buildNrKey, 1)
    }

    fun init() {
        init(null)
    }

    fun init(name: String) {
        init(listOf(name))
    }

    fun init(flavorName: List<String>?) {
        val input = FileInputStream(GRADLE_PROPERTIES_FILE)
        val prop = PropertiesHelper()
        prop.load(input)
        var edit = false
        flavorName?.forEach {
            val key = "build${it.capitalize()}Nr"
            if (!prop.containsKey(key)) {
                prop.setProperty(key, "1")
                edit = true
            }
        }

        if (!prop.containsKey(buildNrKey)) {
            prop.setProperty(buildNrKey, "1")
            edit = true
        }

        input.close()
        if (edit)
            saveProperties(prop)
    }

    private fun saveProperties(properties: PropertiesHelper) {
        val output = FileOutputStream(GRADLE_PROPERTIES_FILE)
        properties.store(output)
        output.close()
    }
}