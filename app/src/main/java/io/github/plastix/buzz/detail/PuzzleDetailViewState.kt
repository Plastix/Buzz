package io.github.plastix.buzz.detail

sealed class PuzzleDetailViewState {
    object Loading : PuzzleDetailViewState()
    data class Error(val error: Exception) : PuzzleDetailViewState()
    data class Success(val boardGameState: BoardGameViewState) : PuzzleDetailViewState()
}

class BoardGameViewState(
    val date: String,
    val centerLetter: Char,
    val outerLetters: List<Char>,
    val currentWord: String,
    val discoveredWords: Set<String>
)
