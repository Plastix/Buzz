package io.github.plastix.buzz.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.plastix.buzz.Puzzle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PuzzleDetailUi(viewState: PuzzleDetailViewState) {
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
    Layout(
        content = {
            KeypadButton(centerLetter)
            KeypadButton(outterLetters.getOrElse(0) { ' ' })
            KeypadButton(outterLetters.getOrElse(1) { ' ' })
            KeypadButton(outterLetters.getOrElse(2) { ' ' })
            KeypadButton(outterLetters.getOrElse(3) { ' ' })
            KeypadButton(outterLetters.getOrElse(4) { ' ' })
            KeypadButton(outterLetters.getOrElse(5) { ' ' })
        },
        modifier = Modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        val spacing = 4.dp.roundToPx()
        val buttonHeight = placeables.first().height
        val totalHeight: Int = (buttonHeight * 3) + (spacing * 2)

        layout(totalHeight, totalHeight) {
            // Place center button
            val viewCx = totalHeight / 2
            val viewCy = totalHeight / 2
            placeables.first().place(viewCx, viewCy)
            // Place surrounding buttons
            placeables.drop(1).forEachIndexed { index, placeable ->
                val offset = buttonHeight + spacing
                val angle = 30 + (index * 60)
                val x = viewCx + cos(angle * (Math.PI / 180)) * offset
                val y = viewCy + sin(angle * (Math.PI / 180)) * offset
                placeable.place(x.toInt(), y.toInt())
            }
        }
    }
}

@Composable
@Preview
fun PreviewPuzzleKeypad() {
    PuzzleKeypad(centerLetter = 'x', outterLetters = listOf('a', 'b', 'c', 'd', 'e', 'f'))
}


@Composable
fun KeypadButton(letter: Char) {
    Button(
        modifier = Modifier.size(64.dp),
        shape = RegularHexagonalShape(),
        onClick = {}
    ) {
        Text(letter.toString())
    }
}

class RegularHexagonalShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val cx = size.width / 2
        val cy = size.height / 2
        var angle = 0
        for (i in 0 until 6) {
            angle += 60
            val x = cos(angle * (Math.PI / 180)) * cx + cx
            val y = sin(angle * (Math.PI / 180)) * cy + cy
            if (i == 0) {
                path.moveTo(x.toFloat(), y.toFloat())
            } else {
                path.lineTo(x.toFloat(), y.toFloat())
            }
        }
        return Outline.Generic(path)
    }
}


@Composable
@Preview
fun PreviewKeypadButton() {
    KeypadButton(letter = 'x')
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

