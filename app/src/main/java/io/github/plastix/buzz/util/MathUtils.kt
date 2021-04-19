package io.github.plastix.buzz.util

import kotlin.math.PI
import kotlin.random.Random

fun Float.radians(): Float {
    return (this / 180f * PI).toFloat()
}

fun randomFloat(min: Float = 0f, max: Float = 1f): Float {
    return min + Random.nextFloat() * (max - min)
}

