package io.github.plastix.buzz.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.plastix.buzz.PuzzleGameState

/**
 * Database model for persisting a [PuzzleGameState].
 */
@Entity(tableName = "game-states")
class PuzzleGameStateEntity(
    @PrimaryKey
    val puzzleId: Long,
    val outerLetters: List<Char>,
    val currentWord: String,
    val discoveredWords: Set<String>
)

fun PuzzleGameStateEntity.toGameState(): PuzzleGameState {
    return PuzzleGameState(
        outerLetters = outerLetters,
        currentWord = currentWord,
        discoveredWords = discoveredWords
    )
}

fun PuzzleGameState.toEntity(puzzleId: Long): PuzzleGameStateEntity {
    return PuzzleGameStateEntity(
        puzzleId = puzzleId,
        outerLetters = outerLetters,
        currentWord = currentWord,
        discoveredWords = discoveredWords
    )
}
