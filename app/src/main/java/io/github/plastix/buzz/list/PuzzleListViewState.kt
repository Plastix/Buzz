package io.github.plastix.buzz.list

import io.github.plastix.buzz.Puzzle

sealed class PuzzleListViewState {
    object Loading : PuzzleListViewState()
    data class Success(val puzzles: List<Puzzle>) : PuzzleListViewState()
}
