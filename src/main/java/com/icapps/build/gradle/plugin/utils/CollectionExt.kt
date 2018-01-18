package com.icapps.build.gradle.plugin.utils

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