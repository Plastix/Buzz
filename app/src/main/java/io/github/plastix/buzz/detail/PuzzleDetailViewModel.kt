package io.github.plastix.buzz.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.plastix.buzz.PuzzleFetcher
import io.github.plastix.buzz.Result
import kotlinx.coroutines.launch

class PuzzleDetailViewModel : ViewModel() {

    private val fetcher = PuzzleFetcher()

    private val puzzleData: MutableLiveData<PuzzleDetailViewState> = MutableLiveData()
    val viewStates: LiveData<PuzzleDetailViewState> = puzzleData

    init {
        loadPuzzleData()
    }

    private fun loadPuzzleData() {
        viewModelScope.launch {
            puzzleData.value = PuzzleDetailViewState.Loading

            val newState = when (val result = fetcher.fetchLatestPuzzles()) {
                is Result.Success -> PuzzleDetailViewState.Success(result.data.first())
                is Result.Error -> PuzzleDetailViewState.Error(result.exception)
            }

            puzzleData.value = newState
        }
    }
}
