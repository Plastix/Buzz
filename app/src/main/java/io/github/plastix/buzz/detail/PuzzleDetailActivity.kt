package io.github.plastix.buzz.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import io.github.plastix.buzz.persistence.PuzzleRepository
import javax.inject.Inject


@AndroidEntryPoint
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

    @Inject
    lateinit var puzzleRepository: PuzzleRepository

    private val viewModel: PuzzleDetailViewModel by viewModels {
        // Dagger hilt does not support assisted injection atm
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PuzzleDetailViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PuzzleDetailViewModel(puzzleId, puzzleRepository) as T
                } else {
                    error("Unknown view model type $modelClass!")
                }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (!viewModel.keyboardEvent(event.unicodeChar)) {
            super.onKeyDown(keyCode, event)
        } else {
            true
        }
    }
}
