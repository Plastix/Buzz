package io.github.plastix.buzz.list

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.plastix.buzz.detail.PuzzleDetailActivity
import io.github.plastix.buzz.settings.SettingsActivity
import io.github.plastix.buzz.util.viewModels
import javax.inject.Inject

@AndroidEntryPoint
class PuzzleListActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: PuzzleListViewModel.Factory

    private val viewModel: PuzzleListViewModel by viewModels { savedState ->
        viewModelFactory.create(savedState)
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
