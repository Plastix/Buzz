package io.github.plastix.buzz.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import io.github.plastix.buzz.persistence.PuzzleRepository

class PuzzleDetailViewModel(
    private val puzzleId: String,
    private val repository: PuzzleRepository
) : ViewModel() {

    private val data: MediatorLiveData<PuzzleDetailViewState> = MediatorLiveData()
    val viewStates: LiveData<PuzzleDetailViewState> = data

    init {
        loadPuzzleData()
    }

    private fun loadPuzzleData() {
        data.value = PuzzleDetailViewState.Loading
        data.addSource(repository.getPuzzle(puzzleId)) { puzzle ->
            data.value = PuzzleDetailViewState.Success(puzzle)
        }
    }
}
