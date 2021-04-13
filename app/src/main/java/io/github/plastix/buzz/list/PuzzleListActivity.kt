package io.github.plastix.buzz.list

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.plastix.buzz.detail.PuzzleDetailActivity
import io.github.plastix.buzz.settings.SettingsActivity
import javax.inject.Inject

@AndroidEntryPoint
class PuzzleListActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: PuzzleListViewModel.Factory

    private val viewModel: PuzzleListViewModel by viewModels {
        PuzzleListViewModel.provideFactory(viewModelFactory, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PuzzleListUi(viewModel, onPuzzleClick = this::openPuzzleDetail, this::openSettings)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveState()
    }

    private fun openPuzzleDetail(puzzleId: Long) {
        startActivity(PuzzleDetailActivity.newIntent(this, puzzleId))
    }

    private fun openSettings() {
        startActivity(SettingsActivity.newIntent(this))
    }
}
