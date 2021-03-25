package io.github.plastix.buzz.detail

import io.github.plastix.buzz.PuzzleRanking

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
    val discoveredWords: Set<String>,
    val currentRank: PuzzleRanking,
    val currentScore: Int,
    val activeDialog: Dialog?
)

sealed class Dialog {
    object ConfirmReset : Dialog()
    object InfoDialog : Dialog()
}
