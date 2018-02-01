package com.icapps.build.gradle.plugin.utils

import org.junit.Assert.*
import org.junit.Test

class ExtTest {

    @Test
    fun testRemoveRegex() {
        val output = " M gradle.properties".removeRegex("\\s*M gradle.properties\\s*")
        assertEquals("Should be empty", "", output)
    }

    @Test
    fun testRemoveLast() {
        val output = "last1 last2 last3".removeLast("last")
        assertEquals("last1 last2 3", output)
    }

    @Test
    fun testRemoveFirst() {
        val output = "first1 first2 first3".removeFirst("first")
        assertEquals("1 first2 first3", output)
    }

    @Test
    fun testReplaceLast() {
        val output = "last1 last2 last3".replaceLast("last", "replace")
        assertEquals("last1 last2 replace3", output)
    }

    @Test
    fun testReplaceFirst() {
        val output = "first1 first2 first3".replaceFirst("first", "replace")
        assertEquals("replace1 first2 first3", output)
    }

    @Test
    fun testNotNullOrEmpty() {
        assertTrue(isNotNullOrEmpty("not-empty"))
        assertFalse(isNotNullOrEmpty(""))
        assertFalse(isNotNullOrEmpty(null))
    }
}