package io.github.plastix.buzz.detail

import android.os.Parcelable
import io.github.plastix.buzz.PuzzleRanking
import io.github.plastix.buzz.WordError
import kotlinx.parcelize.Parcelize

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
    val discoveredPangrams: Set<String>,
    val currentRank: PuzzleRanking,
    val currentScore: Int,
    val activeDialog: Dialog?,
    val activeWordToast: WordToast?,
    val wordBoxExpanded: Boolean
)

sealed class Dialog : Parcelable {
    @Parcelize
    object ConfirmReset : Dialog()

    @Parcelize
    object InfoDialog : Dialog()

    @Parcelize
    data class RankingDialog(val maxPuzzleScore: Int) : Dialog()

    @Parcelize
    data class AnswersDialog(
        val answers: List<String>,
        val found: Set<String>,
        val pangrams: Set<String>
    ) : Dialog()
}

sealed class WordToast : Parcelable {
    @Parcelize
    data class Success(val pointValue: Int) : WordToast()

    @Parcelize
    data class Error(val wordError: WordError) : WordToast()
}
