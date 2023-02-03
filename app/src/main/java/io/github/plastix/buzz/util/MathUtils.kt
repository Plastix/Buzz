package io.github.plastix.buzz.util

import kotlin.math.PI
import kotlin.random.Random

fun Float.radians(): Float {
    return (this / 180f * PI).toFloat()
}

fun randomFloat(min: Float = 0f, max: Float = 1f): Float {
    return min + Random.nextFloat() * (max - min)
}

fun randomInt(min: Int = 0, max: Int = 1): Int {
    return Random.nextInt(min, max)
}

fun remap(x: Float, a0: Float, a1: Float, b0: Float, b1: Float): Float {
    return b0 + (x - a0) * (b1 - b0) / (a1 - a0)
}