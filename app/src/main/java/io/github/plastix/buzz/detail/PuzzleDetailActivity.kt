package io.github.plastix.buzz.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.plastix.buzz.list.PuzzleListViewModel
import io.github.plastix.buzz.network.PuzzleFetcher
import io.github.plastix.buzz.persistence.PuzzleRepository
import io.github.plastix.buzz.persistence.instantiateDatabase


class PuzzleDetailActivity : AppCompatActivity() {

    companion object {
        private const val PUZZLE_ID_KEY: String = "puzzle.id"
        fun newIntent(context: Context, puzzleId: String): Intent {
            return Intent(context, PuzzleDetailActivity::class.java)
                .putExtra(PUZZLE_ID_KEY, puzzleId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this.applicationContext
        val puzzleId: String = intent.getStringExtra(PUZZLE_ID_KEY) ?: error("Expecting puzzle id!")
        // TODO Real dependency injection
        val viewModel: PuzzleDetailViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo =
                        PuzzleRepository(instantiateDatabase(context))
                    return modelClass.getConstructor(
                        String::class.java,
                        PuzzleRepository::class.java
                    ).newInstance(puzzleId, repo)
                }
            }
        }
        setContent {
            val state = viewModel.viewStates.observeAsState(PuzzleDetailViewState.Loading)
            PuzzleDetailUi(state.value)
        }
    }
}
