package com.icapps.build.gradle.plugin.utils

import joptsimple.internal.Strings
import org.gradle.internal.os.OperatingSystem
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * @author Koen Van Looveren
 */
object ShellHelper {

    fun execGit(command: String): String {
        val gitLocation = getGitLocation()
        val cleanCommand = command.replace("git ", "$gitLocation ")
        val process = Runtime.getRuntime().exec(cleanCommand)
        return getOutput(process)
    }

    private fun getGitLocation(): String {
        if (OperatingSystem.current().isWindows) {
            return getGitLocationWindows()
        }
        return getGitLocationLinuxOrMac()
    }

    private fun getGitLocationLinuxOrMac(): String {
        val output = exec("which git")
        if (Strings.isNullOrEmpty(output))
            throw RuntimeException("Git is not installed on your machine")
        return output
    }

    private fun getGitLocationWindows(): String {
        System.getenv("PATH").split(";")
                .map { it + "\\git.exe" }
                .filter { File(it).exists() }
                .forEach { return it }

        val output = exec("where git")
        if (Strings.isNullOrEmpty(output))
            throw RuntimeException("Git is not installed on your machine")
        return output
    }

    fun execGitWithReader(command: String): BufferedReader {
        val rt = Runtime.getRuntime()
        val process = rt.exec(command)
        return BufferedReader(InputStreamReader(process.inputStream))
    }

    fun exec(command: String): String {
        val rt = Runtime.getRuntime()
        val process = rt.exec(command)
        return getOutput(process)
    }

    fun execWithReader(command: String): BufferedReader {
        val rt = Runtime.getRuntime()
        val process = rt.exec(command)
        return BufferedReader(InputStreamReader(process.inputStream))
    }

    private fun getOutput(which: Process): String {
        val reader = BufferedReader(InputStreamReader(which.inputStream))
        val output = StringBuilder()
        for (line in reader.lines()) {
            println(line)
            output.append(line)
        }
        reader.close()
        return output.toString()
    }
}