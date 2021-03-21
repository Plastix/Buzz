package io.github.plastix.buzz.list

import androidx.lifecycle.*
import io.github.plastix.buzz.network.PuzzleFetcher
import io.github.plastix.buzz.Result
import io.github.plastix.buzz.persistence.PuzzleRepository
import kotlinx.coroutines.launch

class PuzzleListViewModel(
    private val fetcher: PuzzleFetcher,
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {

    private val puzzleData: MediatorLiveData<PuzzleListViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleListViewState> = puzzleData

    init {
        observePuzzlesFromDb()
        refreshPuzzleData()
    }

    private fun observePuzzlesFromDb() {
        puzzleData.value = PuzzleListViewState.Loading
        puzzleData.addSource(puzzleRepository.getPuzzles()) { puzzles ->
            puzzleData.value = PuzzleListViewState.Success(puzzles)
        }
    }

    private fun refreshPuzzleData() {
        viewModelScope.launch {
            when (val result = fetcher.fetchLatestPuzzles()) {
                is Result.Success -> puzzleRepository.insertPuzzles(result.data)
                is Result.Error -> {
                    // TODO
                }
            }
        }
    }
}
