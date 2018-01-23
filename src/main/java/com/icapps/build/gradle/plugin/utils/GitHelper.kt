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
        ShellHelper.exec("git add .")
        ShellHelper.exec("git commit -m $message")
    }

    fun ensureCleanRepo() {
        val output = ShellHelper.exec("git status --porcelain")
        if (!Strings.isNullOrEmpty(output))
            throw Exception("Make sure your git is clean")
    }

    fun pushToOrigin() {
        ShellHelper.exec("git push")
    }

    fun getCurrentBranchName(): String {
        val branch = ShellHelper.exec("git branch | grep \\*")
        branch.replace("\\*", "")
        branch.replace("*", "")
        return branch
    }

    fun branchExists(branch: String): Boolean {
        val output = ShellHelper.exec("git show-ref refs/heads/" + branch)
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
        val commitHash = getLatestCommitHash(sinceBranch)
        val input = ShellHelper.execGitWithReader("git log $commitHash.. --pretty=format:\"%s\" --no-merges")
        for (line in input.lines()) {
            messages.add(line)
        }
        input.close()
        return messages
    }

    private fun getLatestCommitHash(branch: String): String {
        return ShellHelper.exec("git log -n 1 $branch --pretty=format:\"%H\"")
    }

    fun getRepoSlug(): String {
        val reader = ShellHelper.execGitWithReader("git remote show origin")
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