package io.github.plastix.buzz.core

import kotlin.math.pow

typealias CharacterSet = Int

/**
 * Converts a string to a character set which is stored as a bit vector.
 */
fun String.toCharacterSet(): CharacterSet {
    var result = 0
    for (c in this) {
        result = result or c.toCharacterSet()
    }
    return result
}

fun Char.toCharacterSet(): CharacterSet {
    val ordinal = this - 'a'
    return 1 shl ordinal
}

fun CharacterSet.toCharArray(): CharArray {
    var set = this
    return CharArray(size) { index ->
        val mask = set.takeLowestOneBit()
        val char = 'a' + mask.countTrailingZeroBits()
        set = set xor mask
        char
    }
}

val CharacterSet.size: Int
    get() = this.countOneBits()

fun CharacterSet.asString(): String {
    return toCharArray().joinToString(separator = "")
}

/**
 * Calculates the power set of this character set
 */
fun CharacterSet.powerSet(): Set<CharacterSet> {
    val activeChars = toCharArray()
    val powerSetSize = 2.0.pow(size).toInt()
    val set = mutableSetOf<CharacterSet>()
    for (counter in 0 until powerSetSize) {
        var item = 0
        for (i in 0 until size) {
            if (counter and (1 shl i) != 0) {
                item = item or (activeChars[i].toCharacterSet())
            }
        }
        set.add(item)
    }
    return set
}
