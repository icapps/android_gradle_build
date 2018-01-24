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

    private const val PATH_KEY = "PATH"

    fun execWithReader(command: String): BufferedReader {
        val executable = getExecutable(command)
        val gitLocation = getCommandLocation(executable)
        val cleanCommand = command.replaceFirst(executable, gitLocation)
        return executeWithReader(cleanCommand)
    }

    fun exec(command: String, newLine: Boolean = false): String {
        val reader = execWithReader(command)
        return getOutput(reader, newLine)
    }

    private fun getExecutable(command: String): String {
        return command.split(" ").firstOrNull()
                ?: throw RuntimeException("Could not execute command: '$command'")
    }

    private fun getCommandLocation(executable: String): String {
        val command = if (OperatingSystem.current().isWindows) {
            "where $executable"
        } else {
            "which $executable"
        }

        val output = execute(command, false)
        if (!Strings.isNullOrEmpty(output))
            return output

        System.getenv(PATH_KEY).split(File.pathSeparator)
                .map { it + File.separator + executable }
                .filter { File(it).exists() }
                .forEach { return it }

        return executable
    }

    private fun execute(command: String, newLine: Boolean): String {
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        return getOutput(reader, newLine)
    }

    private fun executeWithReader(command: String): BufferedReader {
        val process = Runtime.getRuntime().exec(command)
        return BufferedReader(InputStreamReader(process.inputStream))
    }

    private fun getOutput(reader: BufferedReader, newLine: Boolean): String {
        val output = StringBuilder()
        for (line in reader.lines()) {
            output.append(line)
            if (newLine)
                output.append("\n")
        }
        reader.close()
        return output.toString()
    }
}