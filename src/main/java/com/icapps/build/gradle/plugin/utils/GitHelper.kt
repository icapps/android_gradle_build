package com.icapps.build.gradle.plugin.utils

import joptsimple.internal.Strings
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

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
        ShellHelper.executeCommand("git push")
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

    fun getLatestCommitMessages(sinceBranch: String): LinkedList<String> {
        val messages = LinkedList<String>()
        val rt = Runtime.getRuntime()
        val commitHash = getLatestCommitHash(sinceBranch)
        val pr = rt.exec("git log $commitHash.. --pretty=format:\"%s\" --no-merges")
        val input = BufferedReader(InputStreamReader(pr.inputStream))
        for (line in input.lines()) {
            messages.add(line)
        }
        input.close()
        return messages
    }

    private fun getLatestCommitHash(branch: String): String {
        val rt = Runtime.getRuntime()
        val pr = rt.exec("git log -n 1 $branch --pretty=format:\"%H\"")
        val input = BufferedReader(InputStreamReader(pr.inputStream))
        val output = StringBuilder()
        for (line in input.lines()) {
            output.append(line)
        }
        input.close()
        return output.toString()
    }
}