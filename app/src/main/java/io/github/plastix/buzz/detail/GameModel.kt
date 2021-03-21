package io.github.plastix.buzz.detail

/**
 * Representation of a puzzle board state at a moment in time.
 */
data class GameModel(
    val outerLetters: List<Char>,
    val currentWord: String,
    val discoveredWords: Set<String>
)
