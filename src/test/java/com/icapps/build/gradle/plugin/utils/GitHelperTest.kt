package com.icapps.build.gradle.plugin.utils

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Koen Van Looveren
 */
open class GitHelperTest {
    @Test
    open fun branchExists() {
        val exists = GitHelper.branchExists("develop")
        assertTrue("Develop must always exists", exists)
    }
}