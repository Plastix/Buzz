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
import io.github.plastix.buzz.settings.Preferences
import io.github.plastix.buzz.util.minusNull
import io.github.plastix.buzz.util.toDisplayString
import kotlinx.coroutines.launch
import java.util.*


class PuzzleListViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val fetcher: PuzzleFetcher,
    private val puzzleRepository: PuzzleRepository,
    private val preferences: Preferences
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
        private const val HIDDEN_PUZZLES_KEY = "hidden_puzzles"
        private const val ACTIVE_SNACKBAR_KEY = "active_snackbar"
    }

    private val _viewStates: MediatorLiveData<PuzzleListViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleListViewState> = _viewStates

    val newPuzzleConfirmationEnabled: LiveData<Boolean> = preferences.newPuzzleConfirmationEnabled

    private val screenState: MutableLiveData<ScreenState?> = MutableLiveData(null)

    private data class ScreenState(
        val puzzles: List<PuzzleBoardState>,
        val activeDialog: Dialog?,
        val activeSnackbar: Snackbar?,
        // To avoid UI flicker, we keep ids of puzzles that have been swiped away for deletion
        // instead of removing from the repository directly.
        val hiddenPuzzles: Set<Long>
    ) {
        val pendingPuzzleDeletion: Long? =
            (activeSnackbar as? Snackbar.UndoPuzzleDeletion)?.puzzleId
        val puzzlesToDelete: Set<Long> = hiddenPuzzles.minusNull(pendingPuzzleDeletion)
    }

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
            activeDialog = savedStateHandle[ACTIVE_DIALOG_KEY],
            hiddenPuzzles = savedStateHandle[HIDDEN_PUZZLES_KEY] ?: emptySet(),
            activeSnackbar = savedStateHandle[ACTIVE_SNACKBAR_KEY]
        )
    }

    private fun ScreenState.toSuccessState(): PuzzleListViewState.Success {
        return PuzzleListViewState.Success(
            puzzles = puzzles.filterNot { it.puzzle.id in hiddenPuzzles }.map { it.toRowState() },
            activeDialog = activeDialog,
            activeSnackbar = activeSnackbar
        )
    }

    private fun PuzzleBoardState.toRowState(): PuzzleRowState {
        return PuzzleRowState(
            puzzleId = puzzle.id,
            dateString = puzzle.date.toDisplayString(),
            puzzleString = puzzle.centerLetter.plus(puzzle.outerLetters.joinToString(separator = ""))
                .uppercase(Locale.ENGLISH),
            puzzleRank = currentRank,
            currentScore = currentScore,
            type = puzzle.type
        )
    }

    private fun refreshPuzzleData() {
        if (preferences.autoDownloadEnabled()) {
            viewModelScope.launch {
                when (val result = fetcher.fetchLatestPuzzles()) {
                    is Result.Success -> puzzleRepository.insertPuzzles(result.data)
                    is Result.Error -> {
                        // No-op
                    }
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

    fun markPuzzleForDeletion(puzzleId: Long) {
        updateScreenState {
            copy(
                hiddenPuzzles = hiddenPuzzles.plus(puzzleId),
                activeSnackbar = Snackbar.UndoPuzzleDeletion(puzzleId)
            )
        }
    }

    fun undoPendingPuzzleDeletion(puzzleId: Long) {
        updateScreenState {
            copy(
                hiddenPuzzles = hiddenPuzzles.minus(puzzleId),
                activeSnackbar = null
            )
        }
    }

    fun dismissActiveDialog() {
        updateScreenState {
            copy(activeDialog = null)
        }
    }

    fun dismissActiveSnackbar() {
        updateScreenState {
            copy(activeSnackbar = null)
        }
    }

    fun saveState() {
        updateScreenState {
            savedStateHandle[ACTIVE_DIALOG_KEY] = activeDialog
            savedStateHandle[ACTIVE_SNACKBAR_KEY] = activeSnackbar

            // When we save our screen state we can delete all puzzles marked for deletion except
            // any pending deletion
            val newHiddenPuzzles = setOfNotNull(pendingPuzzleDeletion)
            savedStateHandle[HIDDEN_PUZZLES_KEY] = newHiddenPuzzles
            removeDeletedPuzzles()
            copy(hiddenPuzzles = newHiddenPuzzles)
        }
    }

    private fun ScreenState.removeDeletedPuzzles() {
        viewModelScope.launch {
            puzzlesToDelete.forEach { id ->
                try {
                    puzzleRepository.deletePuzzleById(id)
                } catch (e: Exception) {
                    // No-op
                }
            }
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
