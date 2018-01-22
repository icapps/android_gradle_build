package com.icapps.build.gradle.plugin.utils

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Koen Van Looveren
 */
open class GitHelperTest {
    @Test
    open fun branchExists() {
        val branchName = "master"
        val exists = GitHelper.branchExists(branchName)
        assertTrue("$branchName must always exists", exists)
    }

    @Test
    open fun branchNotExists() {
        val branchName = "non-existing-branch"
        val exists = GitHelper.branchExists(branchName)
        assertTrue("$branchName must always exists", exists)
    }
}