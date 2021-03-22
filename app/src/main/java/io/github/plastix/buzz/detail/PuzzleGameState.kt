package io.github.plastix.buzz.detail

import io.github.plastix.buzz.Puzzle

/**
 * Representation of a puzzle board state at a moment in time.
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
