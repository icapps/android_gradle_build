package com.icapps.build.gradle.plugin.utils

import org.junit.Assert.*
import org.junit.Test

class GitHelperTest {

    @Test
    fun testBranchExists() {
        val result = GitHelper.branchExists("feature/bitbucket-integration")
        assertTrue(result)
    }

    @Test
    fun testBranchNotExists() {
        val result = GitHelper.branchExists("not-existing-branch")
        assertFalse(result)
    }
}