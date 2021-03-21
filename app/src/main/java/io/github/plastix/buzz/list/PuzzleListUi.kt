package io.github.plastix.buzz.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import io.github.plastix.buzz.Puzzle
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.ui.text.AnnotatedString

@Composable
fun PuzzleListUi(viewState: PuzzleListViewState, onPuzzleClick: (Puzzle) -> Unit) {
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
fun PuzzleList(puzzles: List<Puzzle>, onPuzzleClick: (Puzzle) -> Unit) {
    LazyColumn {
        items(puzzles) { puzzle ->
            PuzzleRow(puzzle, onPuzzleClick)
        }
    }
}

@Composable
fun PuzzleRow(puzzle: Puzzle, onPuzzleClick: (Puzzle) -> Unit) {
    ClickableText(text = AnnotatedString(puzzle.date), onClick = {
        onPuzzleClick.invoke(puzzle)
    })
}
