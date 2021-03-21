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
)

/**
 * Converts a JSON response to an internal client model.
 */
fun PuzzleResponse.toPuzzle(): Puzzle {
    return Puzzle(
        date = printDate,
        centerLetter = centerLetter[0],
        outerLetters = outerLetters.map { it[0] }.toSet(),
        pangrams = pangrams.toSet(),
        answers = answers.toSet()
    )
}
