package io.github.plastix.buzz.detail

import androidx.lifecycle.*
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.persistence.PuzzleRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PuzzleDetailViewModel(
    private val puzzleId: String,
    private val repository: PuzzleRepository
) : ViewModel() {

    private data class PuzzleDetails(
        val puzzle: Puzzle,
        val gameState: PuzzleGameState
    )

    private val _viewStates: MediatorLiveData<PuzzleDetailViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleDetailViewState> = _viewStates

    private var puzzleData: MutableLiveData<PuzzleDetails?> = MutableLiveData(null)

    init {
        loadPuzzleData()
        listenForBoardChanges()
    }

    private fun listenForBoardChanges() {
        _viewStates.addSource(puzzleData) { puzzleData ->
            if (puzzleData == null) {
                _viewStates.value = PuzzleDetailViewState.Loading
            } else {
                _viewStates.value = PuzzleDetailViewState.Success(puzzleData.constructBoardState())
            }
        }
    }

    private fun loadPuzzleData() {
        viewModelScope.launch {
            // TODO real error handling
            val puzzle: Puzzle =
                repository.getPuzzle(puzzleId) ?: error("Error loading puzzle from db!")
            val gameState = repository.getGameState(puzzleId) ?: puzzle.blankGameState()
            puzzleData.value = PuzzleDetails(puzzle, gameState)
        }
    }

    private fun PuzzleDetails.constructBoardState(): BoardGameViewState {
        return BoardGameViewState(
            date = puzzle.date,
            centerLetter = puzzle.centerLetter,
            outerLetters = gameState.outerLetters,
            currentWord = gameState.currentWord,
            discoveredWords = gameState.discoveredWords
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
    }

    fun delete() {
        updateGameState {
            gameState.copy(currentWord = gameState.currentWord.dropLast(1))
        }
    }

    fun enter() {
        // TODO
    }

    fun saveBoardState() {
        withGameState {
            GlobalScope.launch {
                repository.insertGameState(gameState, puzzleId)
            }
        }
    }

    private fun updateGameState(block: PuzzleDetails.() -> PuzzleGameState) {
        val puzzleDetails = puzzleData.value ?: return
        val newModel = puzzleDetails.block()
        puzzleData.value = puzzleDetails.copy(gameState = newModel)
    }

    private fun withGameState(block: PuzzleDetails.() -> Unit) {
        val puzzleDetails = puzzleData.value ?: return
        puzzleDetails.block()
    }
}
