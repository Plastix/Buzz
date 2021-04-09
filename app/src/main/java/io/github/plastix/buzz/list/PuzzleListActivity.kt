package io.github.plastix.buzz.list

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.plastix.buzz.detail.PuzzleDetailActivity
import io.github.plastix.buzz.settings.SettingsActivity

@AndroidEntryPoint
class PuzzleListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: PuzzleListViewModel by viewModels()
        setContent {
            PuzzleListUi(viewModel, onPuzzleClick = this::openPuzzleDetail, this::openSettings)
        }
    }

    private fun openPuzzleDetail(puzzleId: Long) {
        startActivity(PuzzleDetailActivity.newIntent(this, puzzleId))
    }

    private fun openSettings() {
        startActivity(SettingsActivity.newIntent(this))
    }
}
