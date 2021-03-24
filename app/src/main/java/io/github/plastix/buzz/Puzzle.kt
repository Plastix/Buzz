package io.github.plastix.buzz

/**
 * Internal client representation of a Spelling Bee puzzle.
 */
data class Puzzle(
    val date: String,
    val centerLetter: Char,
    val outerLetters: Set<Char>,
    val pangrams: Set<String>,
    val answers: Set<String>
) {
    fun eligibleLetter(char: Char) : Boolean {
        return centerLetter == char || char in outerLetters
    }
}