package com.icapps.build.gradle.plugin.utils

import joptsimple.internal.Strings
import java.util.*
import java.util.regex.Pattern

/**
 * @author Koen Van Looveren
 */
object GitHelper {

    fun add() {
        ShellHelper.exec(listOf("git", "add", "."))
    }

    fun commit(message: String) {
        ShellHelper.exec(listOf("git", "commit", "-m", message))
    }

    fun addAndCommit(message: String) {
        add()
        commit(message)
    }

    fun ensureCleanRepo() {
        val output = ShellHelper.exec(listOf("git", "status", "--porcelain"))
        if (!Strings.isNullOrEmpty(output))
            throw RuntimeException("Make sure your git repo is clean")
    }

    fun pushToOrigin() {
        ShellHelper.exec(listOf("git", "push"))
    }

    fun getCurrentBranchName(): String {
        val output = ShellHelper.exec(listOf("git", "branch"), newLine = true)
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
        val output = ShellHelper.exec(listOf("git", "show-ref", "refs/heads/" + branch))
        return !Strings.isNullOrEmpty(output)
    }

    fun branchNotExists(branch: String): Boolean {
        return !branchExists(branch)
    }

    fun getLatestCommitMessages(sinceBranch: String): LinkedList<String> {
        val messages = LinkedList<String>()
        val commitHash = getLatestCommitHash(sinceBranch)
        val input = ShellHelper.execWithReader(listOf("git", "log", "$commitHash..", "--pretty=format:%s", "--no-merges"))
        for (line in input.lines()) {
            messages.add(line)
        }
        input.close()
        return messages
    }

    private fun getLatestCommitHash(branch: String): String {
        return ShellHelper.exec(listOf("git", "log", "-n", "1", branch, "-- pretty=format:%H"))
    }

    fun getRepoSlug(): String {
        val output = ShellHelper.exec(listOf("git", "remote", "show", "origin"), newLine = true)
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