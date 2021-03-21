package io.github.plastix.buzz.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.detail.PuzzleDetailActivity
import io.github.plastix.buzz.network.PuzzleFetcher
import io.github.plastix.buzz.persistence.PuzzleRepository
import io.github.plastix.buzz.persistence.instantiateDatabase

class PuzzleListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this.applicationContext
        // TODO Real dependency injection
        val viewModel: PuzzleListViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val fetcher = PuzzleFetcher()
                    val repo =
                        PuzzleRepository(instantiateDatabase(context))
                    return modelClass.getConstructor(
                        PuzzleFetcher::class.java,
                        PuzzleRepository::class.java
                    ).newInstance(fetcher, repo)
                }
            }
        }
        setContent {
            val state = viewModel.viewStates.observeAsState(PuzzleListViewState.Loading)
            PuzzleListUi(state.value, onPuzzleClick = this::openPuzzleDetail)
        }
    }

    private fun openPuzzleDetail(puzzle: Puzzle) {
        startActivity(PuzzleDetailActivity.newIntent(this, puzzle.date))
    }
}
