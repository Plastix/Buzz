package io.github.plastix.buzz.detail

import io.github.plastix.buzz.Puzzle

sealed class PuzzleDetailViewState {
    object Loading : PuzzleDetailViewState()
    data class Error(val error: Exception) : PuzzleDetailViewState()
    data class Success(val puzzle: Puzzle) : PuzzleDetailViewState()
}
