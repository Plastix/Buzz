package io.github.plastix.buzz.list

import io.github.plastix.buzz.PuzzleRanking

sealed class PuzzleListViewState {
    object Loading : PuzzleListViewState()
    data class Success(val puzzles: List<PuzzleRowState>) : PuzzleListViewState()
}

data class PuzzleRowState(
    val puzzleId: Long,
    val displayString: String,
    val puzzleString: String,
    val puzzleRank: PuzzleRanking,
    val currentScore: Int
)
