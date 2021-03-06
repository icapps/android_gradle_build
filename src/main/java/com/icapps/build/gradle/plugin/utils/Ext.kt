package com.icapps.build.gradle.plugin.utils

import joptsimple.internal.Strings
import java.util.regex.Pattern

/**
 * @author Koen Van Looveren
 */
inline fun <T> Collection<T>.exists(function: (T) -> Boolean): Boolean {
    return find(function) != null
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> MutableCollection<T>.replaceAll(vararg items: T): MutableCollection<T> {
    clear()
    addAll(items)
    return this
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> MutableCollection<T>.addAll(vararg items: T): MutableCollection<T> {
    addAll(items)
    return this
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.replaceLast(oldValue: String, newValue: String): String {
    val stringBuilder = StringBuilder(this)
    stringBuilder.replace(this.lastIndexOf(oldValue), this.lastIndexOf(oldValue) + oldValue.length, newValue)
    return stringBuilder.toString()
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.removeFirst(stringToRemove: String): String {
    return this.replaceFirst(stringToRemove, "")
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.removeRegex(regex: String): String {
    val p = Pattern.compile(regex)
    val m = p.matcher(this)
    return m.replaceAll("")
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.removeLast(stringToRemove: String): String {
    return this.replaceLast(stringToRemove, "")
}

fun isNotNullOrEmpty(string: String?): Boolean {
    return !Strings.isNullOrEmpty(string)
}