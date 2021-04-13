package io.github.plastix.buzz.util

fun <T> Set<T>.minusNull(element: T?): Set<T> {
    return if (element != null) {
        this.minus(element)
    } else {
        this
    }
}
