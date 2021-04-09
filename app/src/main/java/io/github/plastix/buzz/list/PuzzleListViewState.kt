package io.github.plastix.buzz.list

import io.github.plastix.buzz.PuzzleRanking
import io.github.plastix.buzz.PuzzleType

sealed class PuzzleListViewState {
    object Loading : PuzzleListViewState()
    data class Success(val puzzles: List<PuzzleRowState>) : PuzzleListViewState()
}

data class PuzzleRowState(
    val puzzleId: Long,
    val dateString: String,
    val puzzleString: String,
    val puzzleRank: PuzzleRanking,
    val currentScore: Int,
    val type: PuzzleType
)
