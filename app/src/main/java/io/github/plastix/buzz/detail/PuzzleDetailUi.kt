package io.github.plastix.buzz.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.graphics.toColorInt
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun PuzzleDetailUi(viewModel: PuzzleDetailViewModel) {
    when (val state = viewModel.viewStates.observeAsState(PuzzleDetailViewState.Loading).value) {
        is PuzzleDetailViewState.Loading -> LoadingState()
        is PuzzleDetailViewState.Success -> PuzzleBoard(
            state.boardGameState,
            onShuffle = viewModel::shuffle,
            onClick = viewModel::keyPress
        )
        is PuzzleDetailViewState.Error -> error(state.error)
    }
}

@Composable
fun PuzzleBoard(response: BoardGameViewState, onShuffle: () -> Unit, onClick: (Char) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(response.currentWord.toUpperCase(), fontSize = 30.sp)
            Spacer(Modifier.height(32.dp))
            PuzzleKeypad(response.centerLetter, response.outerLetters.toList(), onClick)
            Spacer(Modifier.height(32.dp))
            ActionBar(onShuffle = onShuffle)
        }
    }
}

fun String.toColor(): Color = Color(toColorInt())

@Composable
fun PuzzleKeypad(centerLetter: Char, outterLetters: List<Char>, onClick: (Char) -> Unit) {
    Layout(
        content = {
            KeypadButton(centerLetter, "#F8CD05".toColor(), onClick)
            outterLetters.take(6).forEach {
                KeypadButton(it, "#E6E6E6".toColor(), onClick)
            }
        },
        modifier = Modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val width = placeables.first().measuredWidth
        val radius = (width / 2).toDouble()
        val centerToEdge = sqrt(radius.pow(2.0) - (radius / 2.0).pow(2.0)).toInt()
        val height = centerToEdge * 2
        val gap = 30
        val offset = height + gap
        val totalWidth = ((offset * cos(30.0 * (Math.PI / 180)) * 2) + width).toInt()
        val totalHeight = (height * 3) + (gap * 2)
        val centerX = totalWidth / 2 - width / 2
        val centerY = totalHeight / 2 - width / 2

        layout(totalWidth, totalHeight) {
            // Place center button
            placeables.first().place(centerX, centerY)
            // Place surrounding buttons
            placeables.drop(1).forEachIndexed { index, placeable ->
                val angle = 30 + (index * 60)
                val x = centerX + cos(angle * (Math.PI / 180)) * offset
                val y = centerY + sin(angle * (Math.PI / 180)) * offset
                placeable.place(x.toInt(), y.toInt())
            }
        }
    }
}

@Composable
@Preview
fun PreviewPuzzleKeypad() {
    PuzzleKeypad(
        centerLetter = 'x',
        outterLetters = listOf('a', 'b', 'c', 'd', 'e', 'f'),
        onClick = {})
}


@Composable
fun KeypadButton(letter: Char, color: Color = Color.Magenta, onClick: (Char) -> Unit) {
    Button(
        modifier = Modifier.size(100.dp),
        shape = RegularHexagonalShape(),
        onClick = { onClick.invoke(letter) },
        colors = ButtonDefaults.buttonColors(backgroundColor = color)
    ) {
        Text(letter.toUpperCase().toString(), fontSize = 24.sp)
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
    KeypadButton(letter = 'x', onClick = {})
}

@Composable
fun ActionBar(onShuffle: () -> Unit) {
    Row {
        OutlinedButton(onClick = {}) {
            Text("Delete")
        }
        Spacer(Modifier.size(16.dp))
        OutlinedButton(onClick = onShuffle) {
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

