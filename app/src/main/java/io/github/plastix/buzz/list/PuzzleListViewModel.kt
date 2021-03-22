package io.github.plastix.buzz.list

import androidx.lifecycle.*
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.network.PuzzleFetcher
import io.github.plastix.buzz.Result
import io.github.plastix.buzz.persistence.PuzzleRepository
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat

fun reformatDate(date: String, fromFmt: String, toFmt: String): String {
    val fromFormat = SimpleDateFormat(fromFmt)
    val toFormat = SimpleDateFormat(toFmt)
    return try {
        toFormat.format(fromFormat.parse(date)!!)
    } catch (e: ParseException) {
        date
    }
}

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
            displayString = reformatDate(date, "yyyy-MM-dd", "EEEE MMMM d, yyyy")
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
