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

    fun execWithReader(command: Collection<String>): BufferedReader {
        val commandArray = command.toTypedArray()
        val location = getCommandLocation(commandArray[0])
        commandArray[0] = location
        return executeWithReader(commandArray)
    }

    fun exec(command: Collection<String>, newLine: Boolean = false): String {
        val reader = execWithReader(command)
        return getOutput(reader, newLine)
    }

    private fun getCommandLocation(executable: String): String {
        val osSpecificExecutable = if (OperatingSystem.current().isWindows) {
            "$executable.exe"
        } else {
            executable
        }

        val location = getCommandLocationFromEnv(osSpecificExecutable)
        if (osSpecificExecutable == location) {
            val command = if (OperatingSystem.current().isWindows) {
                arrayOf(getCommandLocationFromEnv("where"), osSpecificExecutable)
            } else {
                arrayOf(getCommandLocationFromEnv("which"), osSpecificExecutable)
            }

            val output = execute(command, false)
            if (!Strings.isNullOrEmpty(output))
                return output
        }
        return location
    }

    private fun getCommandLocationFromEnv(executable: String): String {
        System.getenv(PATH_KEY).split(File.pathSeparator)
                .map { it + File.separator + executable }
                .map { File(it) }
                .filter { it.exists() && it.canExecute() }
                .forEach { return it.path }

        return executable
    }

    private fun getProcess(command: Array<String>): Process {
        return ProcessBuilder()
                .redirectErrorStream(true)
                .command(command.toList())
                .start()
    }

    private fun execute(command: Array<String>, newLine: Boolean): String {
        val process = getProcess(command)
        printError(process, command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        return getOutput(reader, newLine)
    }

    private fun executeWithReader(command: Array<String>): BufferedReader {
        val process = getProcess(command)
        printError(process, command)
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

    private fun printError(process: Process, command: Array<String>) {
        val reader = BufferedReader(InputStreamReader(process.errorStream))
        val output = getOutput(reader, true)
        val commandString = command.joinToString(" ")
        if (output.isNotEmpty()) {
            println("===========ERROR==:==$commandString=============")
            println(output)
            println("------------------------------------------")
        }
    }
}