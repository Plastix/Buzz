package io.github.plastix.buzz.detail

import androidx.lifecycle.*
import io.github.plastix.buzz.*
import io.github.plastix.buzz.persistence.PuzzleRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PuzzleDetailViewModel(
    private val puzzleId: String,
    private val repository: PuzzleRepository
) : ViewModel() {

    private val _viewStates: MediatorLiveData<PuzzleDetailViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleDetailViewState> = _viewStates

    private data class DetailState(
        val board: PuzzleBoardState,
        val activeDialog: Dialog?,
        val activeWordToast: WordToast?
    )

    private var detailState: MutableLiveData<DetailState?> = MutableLiveData(null)

    init {
        loadPuzzleData()
        listenForBoardChanges()
    }

    private fun listenForBoardChanges() {
        _viewStates.addSource(detailState) { puzzleData ->
            if (puzzleData == null) {
                _viewStates.value = PuzzleDetailViewState.Loading
            } else {
                _viewStates.value =
                    PuzzleDetailViewState.Success(puzzleData.constructBoardState())
            }
        }
    }

    private fun loadPuzzleData() {
        viewModelScope.launch {
            // TODO real error handling
            val puzzle: Puzzle =
                repository.getPuzzle(puzzleId) ?: error("Error loading puzzle from db!")
            val gameState = repository.getGameState(puzzleId) ?: puzzle.blankGameState()
            detailState.value = DetailState(
                board = PuzzleBoardState(puzzle, gameState),
                activeDialog = null,
                activeWordToast = null
            )
        }
    }

    private fun DetailState.constructBoardState(): BoardGameViewState {
        return BoardGameViewState(
            date = board.puzzle.date,
            centerLetter = board.puzzle.centerLetter,
            outerLetters = board.gameState.outerLetters,
            currentWord = board.gameState.currentWord,
            discoveredWords = board.gameState.discoveredWords,
            currentRank = board.currentRank,
            currentScore = board.currentScore,
            activeDialog = activeDialog,
            activeWordToast = activeWordToast
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

    fun resetConfirmed() {
        updateDetailsState {
            DetailState(
                board.copy(
                    gameState = board.puzzle.blankGameState()
                ),
                activeDialog = null,
                activeWordToast = null
            )
        }
    }

    fun saveBoardState() {
        withGameState {
            GlobalScope.launch {
                repository.insertGameState(gameState, puzzleId)
            }
        }
    }

    fun keyboardEvent(unicodeChar: Int): Boolean {
        var handled = false
        withGameState {
            when (unicodeChar) {
                0 -> {
                    delete()
                    handled = true
                }
                10 -> {
                    enter()
                    handled = true
                }
                32 -> {
                    shuffle()
                    handled = true
                }
                else -> {
                    val char = unicodeChar.toChar()
                    if (puzzle.eligibleLetter(char)) {
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

    private fun withGameState(block: PuzzleBoardState.() -> Unit) {
        val puzzleDetails = detailState.value ?: return
        puzzleDetails.board.block()
    }
}
