package io.github.plastix.buzz

/**
 * Representation of a "modified" puzzle in-play at a moment in time.
 */
data class PuzzleGameState(
    val outerLetters: List<Char>,
    val currentWord: String,
    val discoveredWords: Set<String>
)

fun Puzzle.blankGameState(): PuzzleGameState {
    return PuzzleGameState(
        outerLetters = outerLetters.toList(),
        currentWord = "",
        discoveredWords = emptySet()
    )
}
