package io.github.plastix.buzz.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.BuzzTheme


@Composable
fun PuzzleListUi(viewModel: PuzzleListViewModel, onPuzzleClick: (puzzleId: String) -> Unit) {
    BuzzTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.puzzle_list_title))
                },
            )
        }) {
            PuzzleListScreen(viewModel, onPuzzleClick)
        }
    }
}

@Composable
fun PuzzleListScreen(viewModel: PuzzleListViewModel, onPuzzleClick: (puzzleId: String) -> Unit) {
    when (val viewState = viewModel.viewStates.observeAsState(PuzzleListViewState.Loading).value) {
        is PuzzleListViewState.Loading -> PuzzleListLoadingState()
        is PuzzleListViewState.Success -> {
            if (viewState.puzzles.isEmpty()) {
                PuzzleListEmptyState()
            } else {
                PuzzleList(viewState.puzzles, onPuzzleClick)
            }
        }
    }
}

@Composable
fun PuzzleListEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Filled.SyncProblem,
            contentDescription = stringResource(R.string.puzzle_list_empty_icon),
        )
        Spacer(Modifier.size(16.dp))
        Text(stringResource(R.string.puzzle_list_empty_state), fontSize = 16.sp)
    }
}

@Composable
fun PuzzleListLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
    }
}

@Composable
fun PuzzleList(puzzles: List<PuzzleRowState>, onPuzzleClick: (puzzleId: String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(puzzles) { puzzle ->
            PuzzleRow(puzzle, onPuzzleClick)
        }
    }
}

@Composable
fun PuzzleRow(puzzleRow: PuzzleRowState, onPuzzleClick: (puzzleId: String) -> Unit) {
    ClickableText(text = AnnotatedString(puzzleRow.displayString), onClick = {
        onPuzzleClick.invoke(puzzleRow.puzzleId)
    })
}


@Preview
@Composable
fun PreviewLoadingState() {
    Box(modifier = Modifier.background(White)) {
        PuzzleListLoadingState()
    }
}


@Preview
@Composable
fun PreviewEmptyState() {
    Box(modifier = Modifier.background(White)) {
        PuzzleListEmptyState()
    }
}


@Preview("Puzzle List")
@Composable
fun PreviewPuzzleList() {
    Box(modifier = Modifier.background(White)) {
        PuzzleList(
            puzzles = listOf(
                PuzzleRowState("2021-03-21", "2021-03-21"),
                PuzzleRowState("2021-03-20", "2021-03-20")
            ),
            onPuzzleClick = {}
        )
    }
}
