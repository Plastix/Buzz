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

    private val data: MediatorLiveData<PuzzleDetailViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleDetailViewState> = data
    private var puzzle: Puzzle? = null
    private var gameModel: GameModel? = null

    init {
        loadPuzzleData()
    }

    private fun loadPuzzleData() {
        data.value = PuzzleDetailViewState.Loading
        viewModelScope.launch {
            val puzzle = repository.getPuzzle(puzzleId)
            gameModel = if (repository.hasGameModel(puzzleId)) {
                repository.getGameModel(puzzleId)
            } else {
                GameModel(puzzle.outerLetters.toList(), "", emptySet())
            }
            this@PuzzleDetailViewModel.puzzle = puzzle
            data.value = PuzzleDetailViewState.Success(constructBoardState(puzzle, gameModel!!))
        }
    }

    private fun constructBoardState(puzzle: Puzzle, gameModel: GameModel): BoardGameViewState {
        return BoardGameViewState(
            date = puzzle.date,
            centerLetter = puzzle.centerLetter,
            outerLetters = gameModel.outerLetters,
            currentWord = gameModel.currentWord,
            discoveredWords = gameModel.discoveredWords
        )
    }

    fun shuffle() {
        val gameModel = this.gameModel ?: return
        val puzzle = this.puzzle ?: return
        val newModel = gameModel.copy(outerLetters = gameModel.outerLetters.shuffled())

        data.value = PuzzleDetailViewState.Success(constructBoardState(puzzle, newModel))
        this.gameModel = newModel
    }

    fun keyPress(char: Char) {
        val gameModel = this.gameModel ?: return
        val puzzle = this.puzzle ?: return
        val newModel = gameModel.copy(currentWord = gameModel.currentWord.plus(char))
        data.value = PuzzleDetailViewState.Success(constructBoardState(puzzle, newModel))
        this.gameModel = newModel
    }

    fun saveBoardState() {
        val gameModel = this.gameModel ?: return
        GlobalScope.launch {
            repository.insertGameModel(gameModel, puzzleId)
        }
    }
}
