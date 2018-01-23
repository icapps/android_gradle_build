package com.icapps.build.gradle.plugin.utils

import joptsimple.internal.Strings
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * @author Koen Van Looveren
 */
object ShellHelper {

    fun execGit(command: String): String {
        val rt = Runtime.getRuntime()
        val which = rt.exec("which git")
        val output = getOutput(which)
        if (Strings.isNullOrEmpty(output.toString()))
            throw RuntimeException("Git is not installed")
        val gitPath = File(output)
        val process = rt.exec(command, null, gitPath)
        return getOutput(process)
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