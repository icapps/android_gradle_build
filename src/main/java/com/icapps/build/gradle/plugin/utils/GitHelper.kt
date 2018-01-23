package com.icapps.build.gradle.plugin.utils

import joptsimple.internal.Strings
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern

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

    fun getCurrentBranchName(): String {
        val rt = Runtime.getRuntime()
        val pr = rt.exec("git branch | grep \\*")
        val input = BufferedReader(InputStreamReader(pr.inputStream))
        var branch = ""
        for (line in input.lines()) {
            branch += line
        }
        branch.replace("\\*", "")
        branch.replace("*", "")
        input.close()
        return branch
    }

    fun branchExists(branch: String): Boolean {
        val rt = Runtime.getRuntime()
        val pr = rt.exec("git show-ref refs/heads/" + branch)
        val input = BufferedReader(InputStreamReader(pr.inputStream))
        var output = ""
        for (line in input.lines()) {
            output += line
        }
        input.close()
        if (Strings.isNullOrEmpty(output)) {
            return false
        }
        return true
    }

    fun branchNotExists(branch: String): Boolean {
        return !branchExists(branch)
    }

    fun getLatestCommitMessages(): LinkedList<String> {
        val messages = LinkedList<String>()
        val rt = Runtime.getRuntime()
        val pr = rt.exec("git log --pretty=format:\"%s\" --no-merges")
        val input = BufferedReader(InputStreamReader(pr.inputStream))
        for (line in input.lines()) {
            messages.add(line)
        }
        input.close()
        return messages
    }
}