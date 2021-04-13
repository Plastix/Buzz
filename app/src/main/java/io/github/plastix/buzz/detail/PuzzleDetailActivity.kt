package io.github.plastix.buzz.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PuzzleDetailActivity : AppCompatActivity() {

    companion object {
        private const val PUZZLE_ID_KEY: String = "puzzle.id"
        fun newIntent(context: Context, puzzleId: Long): Intent {
            return Intent(context, PuzzleDetailActivity::class.java)
                .putExtra(PUZZLE_ID_KEY, puzzleId)
        }
    }

    private val puzzleId: Long by lazy {
        intent.getLongExtra(PUZZLE_ID_KEY, -1)
    }

    @Inject
    lateinit var viewModelFactory: PuzzleDetailViewModel.Factory

    private val viewModel: PuzzleDetailViewModel by viewModels {
        PuzzleDetailViewModel.provideFactory(viewModelFactory, this, puzzleId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PuzzleDetailUi(viewModel, this::finish)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveState()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (!viewModel.keyboardEvent(event)) {
            super.onKeyDown(keyCode, event)
        } else {
            true
        }
    }
}
