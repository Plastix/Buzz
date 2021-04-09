package io.github.plastix.buzz.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.plastix.buzz.PuzzleRanking
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.BuzzTheme


@Composable
fun PuzzleListUi(
    viewModel: PuzzleListViewModel,
    onPuzzleClick: (puzzleId: Long) -> Unit,
    onSettings: () -> Unit
) {
    BuzzTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.puzzle_list_title))
                    },
                    actions = {
                        IconButton(onClick = viewModel::newPuzzle) {
                            Icon(
                                imageVector = Icons.Filled.AddCircleOutline,
                                contentDescription = "New Puzzle"
                            )
                        }
                        IconButton(onClick = onSettings) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings_title)
                            )
                        }
                    }
                )
            }
        ) {
            PuzzleListScreen(viewModel, onPuzzleClick)
        }
    }
}

@Composable
fun PuzzleListScreen(
    viewModel: PuzzleListViewModel,
    onPuzzleClick: (puzzleId: Long) -> Unit
) {
    when (val viewState =
        viewModel.viewStates.observeAsState(PuzzleListViewState.Loading).value) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Filled.SyncProblem,
            contentDescription = stringResource(R.string.puzzle_list_empty_icon),
        )
        Spacer(Modifier.size(16.dp))
        Text(
            stringResource(R.string.puzzle_list_empty_state),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
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
fun PuzzleList(puzzles: List<PuzzleRowState>, onPuzzleClick: (puzzleId: Long) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(puzzles) { puzzle ->
            PuzzleRow(puzzle, onPuzzleClick)
        }
    }
}

@Composable
fun PuzzleRow(puzzleRow: PuzzleRowState, onPuzzleClick: (puzzleId: Long) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPuzzleClick.invoke(puzzleRow.puzzleId) },
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                            append(puzzleRow.puzzleString.firstOrNull() ?: ' ')
                        }
                        append(puzzleRow.puzzleString.drop(1))
                    },
                    modifier = Modifier.weight(1f),
                    fontSize = 24.sp,
                    maxLines = 1,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Black
                )
                RankLabel(puzzleRow.puzzleRank, puzzleRow.currentScore)
            }

            Spacer(modifier = Modifier.size(4.dp))
            Text(puzzleRow.displayString, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
fun RankLabel(rank: PuzzleRanking, score: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = rank.displayString),
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.width(4.dp))

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = score.toString(),
                    fontSize = 12.sp,
                )
            }
        }
    }
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
                PuzzleRowState(
                    0, "Monday March 22, 2021", "lenoptu",
                    PuzzleRanking.Genius, 300
                ),
                PuzzleRowState(
                    1, "Sunday March 21, 2021", "lenoptu",
                    PuzzleRanking.GoodStart, 23
                ),
            ),
            onPuzzleClick = {}
        )
    }
}
