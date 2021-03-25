package io.github.plastix.buzz.persistence

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Class which models an inner join between [PuzzleEntity] and [PuzzleGameStateEntity].
 */
data class PuzzleEntityWithGameState(
    @Embedded
    val puzzle: PuzzleEntity,
    @Relation(parentColumn = "puzzleId", entityColumn="puzzleId")
    val gameState: PuzzleGameStateEntity
)