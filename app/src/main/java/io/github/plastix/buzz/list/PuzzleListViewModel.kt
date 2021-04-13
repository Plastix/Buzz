package io.github.plastix.buzz.list

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.plastix.buzz.PuzzleBoardState
import io.github.plastix.buzz.Result
import io.github.plastix.buzz.network.PuzzleFetcher
import io.github.plastix.buzz.persistence.PuzzleRepository
import io.github.plastix.buzz.util.toDisplayString
import kotlinx.coroutines.launch
import java.util.*


class PuzzleListViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val fetcher: PuzzleFetcher,
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): PuzzleListViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            registryOwner: SavedStateRegistryOwner,
        ) = object : AbstractSavedStateViewModelFactory(registryOwner, Bundle()) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return assistedFactory.create(handle) as T
            }
        }

        private const val ACTIVE_DIALOG_KEY = "active_dialog"
    }

    private val _viewStates: MediatorLiveData<PuzzleListViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleListViewState> = _viewStates

    private val screenState: MutableLiveData<ScreenState?> = MutableLiveData(null)

    private data class ScreenState(
        val puzzles: List<PuzzleBoardState>,
        val activeDialog: Dialog? = null,
    )

    init {
        observeStateChanges()
        refreshPuzzleData()
    }

    private fun observeStateChanges() {
        _viewStates.addSource(screenState) { state ->
            val viewState = state?.toSuccessState() ?: PuzzleListViewState.Loading
            _viewStates.value = viewState
        }
        _viewStates.addSource(puzzleRepository.getPuzzles()) { puzzles ->
            val currentState = screenState.value
            val newState = currentState?.copy(puzzles = puzzles)
                ?: constructNewScreenState(puzzles)
            screenState.value = newState
        }
    }

    private fun constructNewScreenState(puzzles: List<PuzzleBoardState>): ScreenState {
        return ScreenState(
            puzzles = puzzles,
            activeDialog = savedStateHandle[ACTIVE_DIALOG_KEY]
        )
    }

    private fun ScreenState.toSuccessState(): PuzzleListViewState.Success {
        return PuzzleListViewState.Success(
            puzzles = puzzles.map { it.toRowState() },
            activeDialog = activeDialog
        )
    }

    private fun PuzzleBoardState.toRowState(): PuzzleRowState {
        return PuzzleRowState(
            puzzleId = puzzle.id,
            dateString = puzzle.date.toDisplayString(),
            puzzleString = puzzle.centerLetter.plus(puzzle.outerLetters.joinToString(separator = ""))
                .toUpperCase(Locale.ENGLISH),
            puzzleRank = currentRank,
            currentScore = currentScore,
            type = puzzle.type
        )
    }

    private fun refreshPuzzleData() {
        viewModelScope.launch {
            when (val result = fetcher.fetchLatestPuzzles()) {
                is Result.Success -> puzzleRepository.insertPuzzles(result.data)
                is Result.Error -> {
                    // No-op
                }
            }
        }
    }

    fun showNewPuzzleDialog() {
        updateScreenState {
            copy(activeDialog = Dialog.ConfirmGeneratePuzzle)
        }
    }

    fun generateNewPuzzle() {
        viewModelScope.launch {
            try {
                puzzleRepository.generateRandomPuzzle()
            } catch (e: Exception) {
                // No-op
            }
        }
    }

    fun dismissActiveDialog() {
        updateScreenState {
            copy(activeDialog = null)
        }
    }

    fun saveState() {
        withScreenState {
            savedStateHandle[ACTIVE_DIALOG_KEY] = activeDialog
        }
    }

    private fun updateScreenState(block: ScreenState.() -> ScreenState) {
        val currentState = screenState.value ?: return
        val newState = block.invoke(currentState)
        screenState.value = newState
    }

    private fun withScreenState(block: ScreenState.() -> Unit) {
        val currentState = screenState.value ?: return
        block.invoke(currentState)
    }
}
