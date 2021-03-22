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

    private val puzzleId: String by lazy {
        intent.getStringExtra(PUZZLE_ID_KEY) ?: error("Expecting puzzle id!")
    }
    private val viewModel: PuzzleDetailViewModel by viewModels {
        // TODO Real dependency injection
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo =
                    PuzzleRepository(instantiateDatabase(this@PuzzleDetailActivity.applicationContext))
                return modelClass.getConstructor(
                    String::class.java,
                    PuzzleRepository::class.java
                ).newInstance(puzzleId, repo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PuzzleDetailUi(viewModel, this::finish)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveBoardState()
    }
}
