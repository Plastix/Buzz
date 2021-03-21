package io.github.plastix.buzz.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.plastix.buzz.Puzzle

@Composable
fun PuzzleDetail(viewState: PuzzleDetailViewState) {
    when (viewState) {
        is PuzzleDetailViewState.Loading -> LoadingState()
        is PuzzleDetailViewState.Success -> PuzzleBoard(viewState.puzzle)
        is PuzzleDetailViewState.Error -> error(viewState.error)
    }
}

@Composable
fun PuzzleBoard(response: Puzzle) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(response.date)
            PuzzleKeypad(response.centerLetter, response.outerLetters.toList())
            Spacer(Modifier.height(32.dp))
            ActionBar()
        }
    }
}

@Composable
fun PuzzleKeypad(centerLetter: Char, outterLetters: List<Char>) {
    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                KeypadButton(outterLetters[0])
                KeypadButton(outterLetters[1])
                KeypadButton(outterLetters[2])
            }
            Spacer(Modifier.size(4.dp))
            KeypadButton(centerLetter)
            Spacer(Modifier.size(4.dp))
            Row {
                KeypadButton(outterLetters[3])
                KeypadButton(outterLetters[4])
                KeypadButton(outterLetters[5])
            }
        }
    }
}

@Composable
fun KeypadButton(letter: Char) {
    Button(
        modifier = Modifier.size(64.dp, 32.dp),
        shape = AbsoluteCutCornerShape(16.dp),
        onClick = {}
    ) {
        Text(letter.toString())
    }
}

@Composable
fun ActionBar() {
    Row {
        OutlinedButton(onClick = {}) {
            Text("Delete")
        }
        Spacer(Modifier.size(16.dp))
        OutlinedButton(onClick = {}) {
            Icon(Icons.Filled.Autorenew, "refresh")
        }
        Spacer(Modifier.size(16.dp))
        OutlinedButton(onClick = {}) {
            Text("Enter")
        }
    }
}

@Composable
fun LoadingState() {
    Box(contentAlignment = Alignment.Center) {
        Text("Puzzle loading")
    }
}

