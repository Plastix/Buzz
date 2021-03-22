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
        val gameModel: GameModel
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
            val gameModel = repository.getGameModel(puzzleId) ?: puzzle.blankGameModel()
            puzzleData.value = PuzzleDetails(puzzle, gameModel)
        }
    }

    private fun PuzzleDetails.constructBoardState(): BoardGameViewState {
        return BoardGameViewState(
            date = puzzle.date,
            centerLetter = puzzle.centerLetter,
            outerLetters = gameModel.outerLetters,
            currentWord = gameModel.currentWord,
            discoveredWords = gameModel.discoveredWords
        )
    }

    fun shuffle() {
        updateGameModel {
            gameModel.copy(outerLetters = gameModel.outerLetters.shuffled())
        }
    }

    fun keyPress(char: Char) {
        updateGameModel {
            gameModel.copy(currentWord = gameModel.currentWord.plus(char))
        }
    }

    fun saveBoardState() {
        withGameModel {
            GlobalScope.launch {
                repository.insertGameModel(gameModel, puzzleId)
            }
        }
    }

    private fun updateGameModel(block: PuzzleDetails.() -> GameModel) {
        val puzzleDetails = puzzleData.value ?: return
        val newModel = puzzleDetails.block()
        puzzleData.value = puzzleDetails.copy(gameModel = newModel)
    }

    private fun withGameModel(block: PuzzleDetails.() -> Unit) {
        val puzzleDetails = puzzleData.value ?: return
        puzzleDetails.block()
    }
}
