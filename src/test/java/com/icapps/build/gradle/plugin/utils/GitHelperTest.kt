package com.icapps.build.gradle.plugin.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * @author Koen Van Looveren
 */
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
        assertEquals("master", result)
    }

    @Test
    fun testLatestMessages() {
        val result = GitHelper.getLatestCommitMessages("master")
        assertTrue(result.isNotEmpty())
    }
}