package io.github.plastix.buzz.detail

import androidx.lifecycle.*
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.PuzzleRanking
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
    ) {
        val currentScore: Int = gameState.discoveredWords.sumBy { word -> puzzle.scoreWord(word) }
        private val currentPercent: Int =
            ((currentScore / puzzle.maxScore.toDouble()) * 100).toInt()
        val currentRank: PuzzleRanking = PuzzleRanking.values()
            .filter { rank -> rank.percentCutoff <= currentPercent }
            .maxByOrNull { rank -> rank.percentCutoff } ?: PuzzleRanking.Beginner
    }

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
            discoveredWords = gameState.discoveredWords,
            currentRank = currentRank,
            currentScore = currentScore
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
        updateGameState {
            // TODO Actual word validation
            val enteredWord = gameState.currentWord
            val discoveredSet = gameState.discoveredWords.plus(enteredWord)
            gameState.copy(currentWord = "", discoveredWords = discoveredSet)
        }
    }

    fun resetGame() {
        // TODO confirmation dialog
        updateGameState {
            puzzle.blankGameState()
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
