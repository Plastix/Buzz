package io.github.plastix.buzz.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PuzzleListUi(viewState: PuzzleListViewState, onPuzzleClick: (puzzleId: String) -> Unit) {
    when (viewState) {
        is PuzzleListViewState.Loading -> {
        }
        is PuzzleListViewState.Success -> {
            if (viewState.puzzles.isEmpty()) {
                EmptyState()
            } else {
                PuzzleList(viewState.puzzles, onPuzzleClick)
            }
        }
    }
}

@Composable
fun EmptyState() {
    Text("No downloaded puzzles!")
}

@Composable
fun PuzzleList(puzzles: List<PuzzleRowState>, onPuzzleClick: (puzzleId: String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
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

@Preview("Puzzle List")
@Composable
fun PreviewPuzzleList() {
    PuzzleList(puzzles = listOf(
        PuzzleRowState("2021-03-21", "2021-03-21"),
        PuzzleRowState("2021-03-20", "2021-03-20")
    )) {}
}
