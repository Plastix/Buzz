package io.github.plastix.buzz.detail

import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.plastix.buzz.*
import io.github.plastix.buzz.persistence.PuzzleRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PuzzleDetailViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted private val puzzleId: Long,
    private val repository: PuzzleRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle, puzzleId: Long): PuzzleDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            registryOwner: SavedStateRegistryOwner,
            puzzleId: Long
        ) = object : AbstractSavedStateViewModelFactory(registryOwner, Bundle()) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return assistedFactory.create(handle, puzzleId) as T
            }
        }

        private const val ACTIVE_DIALOG_KEY = "active_dialog"
        private const val ACTIVE_WORD_TOAST_KEY = "active_word_toast"
        private const val WORD_BOX_EXPANDED_KEY = "word_box_expanded"
    }

    private val _viewStates: MediatorLiveData<PuzzleDetailViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleDetailViewState> = _viewStates

    private data class DetailState(
        val board: PuzzleBoardState,
        val activeDialog: Dialog?,
        val activeWordToast: WordToast?,
        val wordBoxExpanded: Boolean
    )

    private val detailState: MutableLiveData<DetailState?> = MutableLiveData(null)

    init {
        loadPuzzleData()
        listenForBoardChanges()
    }

    private fun listenForBoardChanges() {
        _viewStates.addSource(detailState) { puzzleData ->
            val newViewState = if (puzzleData == null) {
                PuzzleDetailViewState.Loading
            } else {
                val boardState = puzzleData.constructBoardState()
                PuzzleDetailViewState.Success(boardState)
            }
            _viewStates.value = newViewState
        }
    }

    private fun loadPuzzleData() {
        viewModelScope.launch {
            try {
                val puzzle: Puzzle =
                    repository.getPuzzle(puzzleId)
                        ?: error("Expecting puzzle in database for id $puzzleId")
                val gameState = repository.getGameState(puzzleId) ?: puzzle.blankGameState()
                detailState.value = constructDetailStateState(puzzle, gameState)
            } catch (e: Exception) {
                _viewStates.value = PuzzleDetailViewState.Error(e)
            }
        }
    }

    private fun constructDetailStateState(puzzle: Puzzle, gameState: PuzzleGameState): DetailState {
        return DetailState(
            board = PuzzleBoardState(puzzle, gameState),
            activeDialog = savedStateHandle[ACTIVE_DIALOG_KEY],
            activeWordToast = savedStateHandle[ACTIVE_WORD_TOAST_KEY],
            wordBoxExpanded = savedStateHandle[WORD_BOX_EXPANDED_KEY] ?: false
        )
    }

    private fun DetailState.constructBoardState(): BoardGameViewState {
        return BoardGameViewState(
            centerLetter = board.puzzle.centerLetter,
            outerLetters = board.gameState.outerLetters,
            currentWord = board.gameState.currentWord,
            discoveredWords = board.gameState.discoveredWords,
            discoveredPangrams = board.gameState.discoveredWords
                .filter { word -> word in board.puzzle.pangrams }.toSet(),
            currentRank = board.currentRank,
            currentScore = board.currentScore,
            activeDialog = activeDialog,
            activeWordToast = activeWordToast,
            wordBoxExpanded = wordBoxExpanded
        )
    }

    fun shuffle() {
        updateGameState {
            gameState.copy(outerLetters = gameState.outerLetters.shuffled())
        }
    }

    fun keypress(char: Char) {
        updateGameState {
            gameState.copy(currentWord = gameState.currentWord.plus(char))
        }

        withGameState {
            if (gameState.currentWord.length > Puzzle.MAX_WORD_LENGTH) {
                enter()
            }
        }
    }

    fun delete() {
        updateGameState {
            gameState.copy(currentWord = gameState.currentWord.dropLast(1))
        }
    }

    fun enter() {
        updateDetailsState {
            val gameState = board.gameState
            val enteredWord = gameState.currentWord
            if (enteredWord.isBlank()) {
                // Don't accept empty words
                this
            } else {
                when (val wordResult = board.validateWord(enteredWord)) {
                    is WordResult.Valid -> {
                        val discoveredSet = gameState.discoveredWords.plus(enteredWord)
                        copy(
                            board = board.copy(
                                gameState = gameState.copy(
                                    currentWord = "",
                                    discoveredWords = discoveredSet
                                )
                            ),
                            activeWordToast = WordToast.Success(board.puzzle.scoreWord(enteredWord))
                        )
                    }

                    is WordResult.Error -> {
                        copy(
                            board = board.copy(
                                gameState = gameState.copy(currentWord = "")
                            ),
                            activeWordToast = WordToast.Error(wordResult.errorType)
                        )
                    }
                }
            }
        }
    }

    fun dismissActiveDialog() {
        updateDetailsState {
            copy(activeDialog = null)
        }
    }

    fun dismissActiveToast() {
        updateDetailsState {
            copy(activeWordToast = null)
        }
    }

    fun resetGame() {
        updateDetailsState {
            copy(activeDialog = Dialog.ConfirmReset)
        }
    }

    fun infoIconClicked() {
        updateDetailsState {
            copy(activeDialog = Dialog.InfoDialog)
        }
    }

    fun scoreBarClicked() {
        updateDetailsState {
            copy(activeDialog = Dialog.RankingDialog(board.puzzle.maxScore))
        }
    }

    fun resetConfirmed() {
        updateDetailsState {
            DetailState(
                board.copy(
                    gameState = board.puzzle.blankGameState()
                ),
                activeDialog = null,
                activeWordToast = null,
                wordBoxExpanded = false
            )
        }
    }

    fun toggleWorldBox() {
        updateDetailsState {
            copy(wordBoxExpanded = !wordBoxExpanded)
        }
    }

    fun saveBoardState() {
        withDetailsState {
            GlobalScope.launch {
                repository.insertGameState(board.gameState, puzzleId)
            }
            savedStateHandle[ACTIVE_DIALOG_KEY] = activeDialog
            savedStateHandle[ACTIVE_WORD_TOAST_KEY] = activeWordToast
            savedStateHandle[WORD_BOX_EXPANDED_KEY] = wordBoxExpanded
        }
    }

    fun keyboardEvent(event: KeyEvent): Boolean {
        var handled = false
        withDetailsState {
            when (val unicodeChar = event.unicodeChar) {
                0 -> {
                    handled = when (event.keyCode) {
                        KeyCodes.DELETE -> {
                            delete()
                            true
                        }
                        KeyCodes.BACK -> {
                            if (wordBoxExpanded) {
                                toggleWorldBox()
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                }
                KeyCodes.ENTER -> {
                    enter()
                    handled = true
                }
                KeyCodes.SPACE -> {
                    shuffle()
                    handled = true
                }
                else -> {
                    val char = unicodeChar.toChar()
                    if (board.puzzle.eligibleLetter(char)) {
                        keypress(char)
                        handled = true
                    }
                }
            }
        }
        return handled
    }

    private fun updateGameState(block: PuzzleBoardState.() -> PuzzleGameState) {
        val puzzleDetails = detailState.value ?: return
        val newModel = puzzleDetails.board.block()
        detailState.value =
            puzzleDetails.copy(
                board = puzzleDetails.board.copy(
                    gameState = newModel
                )
            )
    }

    private fun updateDetailsState(block: DetailState.() -> DetailState) {
        val puzzleDetails = detailState.value ?: return
        val newModel = puzzleDetails.block()
        detailState.value = newModel
    }

    private fun withDetailsState(block: DetailState.() -> Unit) {
        val puzzleDetails = detailState.value ?: return
        puzzleDetails.block()
    }

    private fun withGameState(block: PuzzleBoardState.() -> Unit) {
        val puzzleDetails = detailState.value ?: return
        puzzleDetails.board.block()
    }
}
