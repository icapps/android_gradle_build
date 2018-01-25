package com.icapps.build.gradle.plugin.utils

import org.junit.Assert.*
import org.junit.Test

class GitHelperTest {

    @Test
    fun testBranchExists() {
        val result = GitHelper.branchExists("master")
        assertTrue(result)
    }

    @Test
    fun testBranchNotExists() {
        val result = GitHelper.branchExists("not-existing-branch")
        assertFalse(result)
    }

    @Test
    fun testCurrentBranch() {
        val result = GitHelper.getCurrentBranchName()
        assertEquals("feature/hockey-release-notes", result)
    }

    @Test
    fun testLatestMessages() {
        val result = GitHelper.getLatestCommitMessages("master")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testCommit() {
        GitHelper.commit("Commit from test")
    }
}