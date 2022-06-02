package io.github.plastix.buzz.detail

import androidx.lifecycle.LiveData

interface DetailScreen {
    val viewStates: LiveData<PuzzleDetailViewState>

    fun shuffle()
    fun keypress(char: Char)
    fun delete()
    fun enter()
    fun dismissActiveDialog()
    fun dismissActiveToast()
    fun resetGame()
    fun infoIconClicked()
    fun scoreBarClicked()
    fun resetConfirmed()
    fun toggleWorldBox()
    fun solvePuzzle()
    fun showAnswersDialog()
}