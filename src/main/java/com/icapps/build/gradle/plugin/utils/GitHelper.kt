package com.icapps.build.gradle.plugin.utils

import joptsimple.internal.Strings
import java.util.*
import java.util.regex.Pattern

/**
 * @author Koen Van Looveren
 */
object GitHelper {

    fun commit(message: String) {
        ShellHelper.exec("git add .")
        ShellHelper.exec("git commit -m \"$message\"")
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
        val output = ShellHelper.exec("git branch", newLine = true)
        val regex = "\\* (.*)"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(output)
        if (matcher.find()) {
            return matcher.group(1)
        } else {
            throw RuntimeException("Could not parse your origin url.")
        }
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
        val input = ShellHelper.execWithReader("git log $commitHash.. --pretty=format:%s --no-merges")
        for (line in input.lines()) {
            messages.add(line)
        }
        input.close()
        return messages
    }

    private fun getLatestCommitHash(branch: String): String {
        return ShellHelper.exec("git log -n 1 $branch --pretty=format:%H")
    }

    fun getRepoSlug(): String {
        val output = ShellHelper.exec("git remote show origin", newLine = true)
        val regex = "/(.*).git"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(output)
        if (matcher.find()) {
            return matcher.group(1)
        } else {
            throw RuntimeException("Could not parse your origin url.")
        }
    }
}