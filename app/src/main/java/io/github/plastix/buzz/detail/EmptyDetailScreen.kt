package io.github.plastix.buzz.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * No-op implementation of [DetailScreen] used to render Compose previews
 */
class EmptyDetailScreen : DetailScreen {
    override val viewStates: LiveData<PuzzleDetailViewState>
        get() = MutableLiveData(PuzzleDetailViewState.Loading)

    override fun shuffle() {}

    override fun keypress(char: Char) {}

    override fun delete() {}

    override fun enter() {}

    override fun dismissActiveDialog() {}

    override fun dismissActiveToast() {}

    override fun resetGame() {}

    override fun infoIconClicked() {}

    override fun scoreBarClicked() {}

    override fun resetConfirmed() {}

    override fun toggleWorldBox() {}

    override fun solvePuzzle() {}
}