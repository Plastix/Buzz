package io.github.plastix.buzz.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.Result
import io.github.plastix.buzz.formatDate
import io.github.plastix.buzz.network.PuzzleFetcher
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
            val states = puzzles.map { it.toRowState() }
            puzzleData.value = PuzzleListViewState.Success(states)
        }
    }

    private fun Puzzle.toRowState(): PuzzleRowState {
        return PuzzleRowState(
            puzzleId = date,
            displayString = formatDate(date)
        )
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
