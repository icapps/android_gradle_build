package com.icapps.build.gradle.plugin.utils

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author Koen Van Looveren
 */
object ShellHelper {

    fun executeCommand(command: String) {
        val rt = Runtime.getRuntime()
        val pr = rt.exec(command)
        val input = BufferedReader(InputStreamReader(pr.inputStream))
        for (line in input.lines()) {
            println(line)
        }
        input.close()
    }
}