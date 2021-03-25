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