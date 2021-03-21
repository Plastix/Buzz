package io.github.plastix.buzz.list

sealed class PuzzleListViewState {
    object Loading : PuzzleListViewState()
    data class Success(val puzzles: List<PuzzleRowState>) : PuzzleListViewState()
}

data class PuzzleRowState(
    val puzzleId: String,
    val displayString: String
)
