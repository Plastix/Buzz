package io.github.plastix.buzz.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import io.github.plastix.buzz.Puzzle
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text

@Composable
fun PuzzleListUi(viewState: PuzzleListViewState) {
    when (viewState) {
        is PuzzleListViewState.Loading -> {
        }
        is PuzzleListViewState.Success -> {
            if (viewState.puzzles.isEmpty()) {
                EmptyState()
            } else {
                PuzzleList(viewState.puzzles)
            }
        }
    }
}

@Composable
fun EmptyState() {
    Text("No downloaded puzzles!")
}

@Composable
fun PuzzleList(puzzles: List<Puzzle>) {
    LazyColumn {
        items(puzzles) { puzzle ->
            PuzzleRow(puzzle)
        }
    }
}

@Composable
fun PuzzleRow(puzzle: Puzzle) {
    Text(puzzle.date)
}
