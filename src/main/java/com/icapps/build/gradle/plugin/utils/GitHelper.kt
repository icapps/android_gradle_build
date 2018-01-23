package com.icapps.build.gradle.plugin.utils

import jdk.nashorn.tools.Shell
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
        val output = ShellHelper.executeCommand("git status --porcelain")
        if (!Strings.isNullOrEmpty(output))
            throw Exception("Make sure your git is clean")
    }

    fun pushToOrigin() {
        ShellHelper.executeCommand("git push")
    }

    fun getCurrentBranchName(): String {
        val branch = ShellHelper.executeCommand("git branch | grep \\*")
        branch.replace("\\*", "")
        branch.replace("*", "")
        return branch
    }

    fun branchExists(branch: String): Boolean {
        val output = ShellHelper.executeCommand("git show-ref refs/heads/" + branch)
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
        return ShellHelper.executeCommand("git log -n 1 $branch --pretty=format:\"%H\"")
    }

    fun getRepoSlug(): String {
        val p = Runtime.getRuntime().exec("git remote show origin")
        val reader = BufferedReader(InputStreamReader(p.inputStream))
        val output = StringBuilder()
        for (line in reader.lines()) {
            output.append(line).append("\n")
        }
        reader.close()
        val regex = "/(.*).git"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(output.toString())
        if (matcher.find()) {
            return matcher.group(1)
        } else {
            throw RuntimeException("Could not parse your origin url.")
        }
    }
}