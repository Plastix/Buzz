package io.github.plastix.buzz.detail

import io.github.plastix.buzz.PuzzleRanking
import io.github.plastix.buzz.WordError

sealed class PuzzleDetailViewState {
    object Loading : PuzzleDetailViewState()
    data class Error(val error: Exception) : PuzzleDetailViewState()
    data class Success(val boardGameState: BoardGameViewState) : PuzzleDetailViewState()
}

class BoardGameViewState(
    val centerLetter: Char,
    val outerLetters: List<Char>,
    val currentWord: String,
    val discoveredWords: Set<String>,
    val currentRank: PuzzleRanking,
    val currentScore: Int,
    val activeDialog: Dialog?,
    val activeWordToast: WordToast?
)

sealed class Dialog {
    object ConfirmReset : Dialog()
    object InfoDialog : Dialog()
    data class RankingDialog(val maxPuzzleScore: Int) : Dialog()
}

sealed class WordToast {
    data class Success(val pointValue: Int) : WordToast()
    data class Error(val wordError: WordError) : WordToast()
}
