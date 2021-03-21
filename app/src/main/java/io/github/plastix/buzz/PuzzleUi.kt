package io.github.plastix.buzz

import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PuzzleDetail(response: PuzzleResponse?) {
    if (response == null) {
        LoadingState()
    } else {
        PuzzleBoard(response)
    }
}

@Composable
fun PuzzleBoard(response: PuzzleResponse) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(response.printDate)
            Text(response.editor)
            PuzzleKeypad(response.centerLetter, response.outerLetters)
            Spacer(Modifier.height(32.dp))
            ActionBar()
        }
    }
}

@Composable
fun PuzzleKeypad(centerLetter: String, outterLetters: List<String>) {
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
fun KeypadButton(letter: String) {
    Button(
        modifier = Modifier.size(64.dp, 32.dp),
        shape = AbsoluteCutCornerShape(16.dp),
        onClick = {}
    ) {
        Text(letter[0].toString())
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

@Preview
@Composable
fun DefaultPreview() {
    PuzzleDetail(null)
}

