package io.github.plastix.buzz

import java.time.LocalDateTime

/**
 * Internal client representation of a Spelling Bee puzzle.
 */
data class Puzzle(
    val id: Long,
    val date: LocalDateTime,
    val centerLetter: Char,
    val outerLetters: Set<Char>,
    val pangrams: Set<String>,
    val answers: Set<String>,
    val type: PuzzleType,
) {

    companion object {
        const val MIN_WORD_LENGTH = 4
        const val MAX_WORD_LENGTH = 19
        const val AUTO_GENERATE_ID = 0L
    }

    val maxScore: Int = answers.sumBy(::scoreWord)

    fun eligibleLetter(char: Char): Boolean {
        return centerLetter == char || char in outerLetters
    }

    fun scoreWord(word: String): Int {
        if (word !in answers) return 0
        if (word.length == 4) return 1
        return if (word in pangrams) {
            word.length + 7
        } else {
            word.length
        }
    }
}