package io.github.plastix.buzz.list

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.plastix.buzz.detail.PuzzleDetailActivity

@AndroidEntryPoint
class PuzzleListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: PuzzleListViewModel by viewModels()
        setContent {
            PuzzleListUi(viewModel, onPuzzleClick = this::openPuzzleDetail)
        }
    }

    private fun openPuzzleDetail(puzzleId: String) {
        startActivity(PuzzleDetailActivity.newIntent(this, puzzleId))
    }
}
