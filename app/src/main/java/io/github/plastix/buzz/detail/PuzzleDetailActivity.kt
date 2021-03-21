package io.github.plastix.buzz.detail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.livedata.observeAsState


class PuzzleDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: PuzzleDetailViewModel by viewModels()
        setContent {
            val state = viewModel.viewStates.observeAsState(PuzzleDetailViewState.Loading)
            PuzzleDetailUi(state.value)
        }
    }
}
