package io.github.plastix.buzz.list

import android.os.Parcelable
import io.github.plastix.buzz.PuzzleRanking
import io.github.plastix.buzz.PuzzleType
import kotlinx.parcelize.Parcelize

sealed class PuzzleListViewState {
    data object Loading : PuzzleListViewState()
    data class Success(
        val puzzles: List<PuzzleRowState>,
        val activeDialog: Dialog?,
        val activeSnackbar: Snackbar?
    ) : PuzzleListViewState()
}

data class PuzzleRowState(
    val puzzleId: Long,
    val dateString: String,
    val puzzleString: String,
    val puzzleRank: PuzzleRanking,
    val currentScore: Int,
    val type: PuzzleType
)

sealed class Dialog : Parcelable {
    @Parcelize
    data object ConfirmGeneratePuzzle : Dialog()
}

sealed class Snackbar : Parcelable {
    @Parcelize
    data class UndoPuzzleDeletion(val puzzleId: Long) : Snackbar()
}
