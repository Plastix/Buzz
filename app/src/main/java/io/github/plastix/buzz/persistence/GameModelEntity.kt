package io.github.plastix.buzz.persistence

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.detail.GameModel

/**
 * Database model for persisting a [GameModel].
 */
@Entity(tableName = "game-states")
class GameModelEntity(
    @PrimaryKey
    val puzzleId: String,
    val outerLetters: List<Char>,
    val currentWord: String,
    val discoveredWords: Set<String>
)

fun GameModelEntity.toGameModel(): GameModel {
    return GameModel(
        outerLetters = outerLetters,
        currentWord = currentWord,
        discoveredWords = discoveredWords
    )
}

fun GameModel.toEntity(puzzleId: String): GameModelEntity {
    return GameModelEntity(
        puzzleId = puzzleId,
        outerLetters = outerLetters,
        currentWord = currentWord,
        discoveredWords = discoveredWords
    )
}
