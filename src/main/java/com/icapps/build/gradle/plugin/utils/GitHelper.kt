package com.icapps.build.gradle.plugin.utils

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author Koen Van Looveren
 */
object GitHelper {

    fun commit(message: String, vararg paths: String) {
        ShellHelper.executeCommand("git add .")
        ShellHelper.executeCommand("git commit -m $message")
    }

    fun ensureCleanRepo() {
        val rt = Runtime.getRuntime()
        val pr = rt.exec("git status --porcelain")
        val input = BufferedReader(InputStreamReader(pr.inputStream))
        if (input.readLine() != null) {
            input.close()
            throw Exception("Make sure your git is clean")
        }
        input.close()
    }

    fun pushToOrigin() {
        println("Git: Push to origin")
    }
}