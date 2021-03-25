package io.github.plastix.buzz.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.plastix.buzz.PuzzleRanking
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.BuzzTheme
import java.util.*
import kotlin.math.*

@Composable
fun PuzzleDetailUi(
    viewModel: PuzzleDetailViewModel,
    onBack: () -> Unit
) {
    BuzzTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.puzzle_detail_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.puzzle_detail_title)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::resetGame) {
                        Icon(
                            imageVector = Icons.Filled.Replay,
                            contentDescription = stringResource(R.string.puzzle_detail_toolbar_reset)
                        )
                    }
                }
            )
        }) {
            PuzzleDetailScreen(viewModel)
        }
    }
}

@Composable
fun PuzzleDetailScreen(viewModel: PuzzleDetailViewModel) {
    when (val state =
        viewModel.viewStates.observeAsState(PuzzleDetailViewState.Loading).value) {
        is PuzzleDetailViewState.Loading -> PuzzleDetailLoadingState()
        is PuzzleDetailViewState.Success -> PuzzleBoard(
            state.boardGameState,
            viewModel,
            onShuffle = viewModel::shuffle,
            onKeyClick = viewModel::keypress,
            onDelete = viewModel::delete,
            onEnter = viewModel::enter
        )
        is PuzzleDetailViewState.Error -> error(state.error)
    }
}

@Composable
fun PuzzleBoard(
    state: BoardGameViewState,
    viewModel: PuzzleDetailViewModel,
    onShuffle: () -> Unit,
    onKeyClick: (Char) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ScoreBox(state.currentRank, state.currentScore)
            Spacer(Modifier.height(12.dp))
            DiscoveredWordBox(words = state.discoveredWords)
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InputBox(centerLetter = state.centerLetter, word = state.currentWord)
                Spacer(Modifier.height(32.dp))
                PuzzleKeypad(state.centerLetter, state.outerLetters.toList(), onKeyClick)
                Spacer(Modifier.height(32.dp))
                ActionBar(onShuffle = onShuffle, onDelete = onDelete, onEnter = onEnter)
            }
        }
        if (state.activeDialog != null) {
            ShowDialog(viewModel, state.activeDialog)
        }
    }
}

@Composable
fun ShowDialog(viewModel: PuzzleDetailViewModel, activeDialog: Dialog) {
    when (activeDialog) {
        is Dialog.ConfirmReset -> ShowResetConfirmationDialog(viewModel)
    }
}

@Composable
fun ShowResetConfirmationDialog(viewModel: PuzzleDetailViewModel) {
    AlertDialog(onDismissRequest = viewModel::dismissActiveDialog,
        title = { Text(stringResource(R.string.puzzle_detail_reset_confirm_title)) },
        text = { Text(stringResource(R.string.puzzle_detail_reset_confirm_body)) },
        confirmButton = {
            TextButton(onClick = {
                viewModel.dismissActiveDialog()
                viewModel.resetConfirmed()
            }) {
                Text(stringResource(R.string.puzzle_detail_reset_confirm_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::dismissActiveDialog) {
                Text(stringResource(R.string.puzzle_detail_reset_confirm_cancel))
            }
        }
    )
}

@Composable
fun ScoreBox(rank: PuzzleRanking, score: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val rankString = when (rank) {
            PuzzleRanking.Beginner -> stringResource(R.string.puzzle_rank_beginner)
            PuzzleRanking.GoodStart -> stringResource(R.string.puzzle_rank_goodstart)
            PuzzleRanking.MovingUp -> stringResource(R.string.puzzle_rank_movingup)
            PuzzleRanking.Good -> stringResource(R.string.puzzle_rank_good)
            PuzzleRanking.Solid -> stringResource(R.string.puzzle_rank_solid)
            PuzzleRanking.Nice -> stringResource(R.string.puzzle_rank_nice)
            PuzzleRanking.Great -> stringResource(R.string.puzzle_rank_great)
            PuzzleRanking.Amazing -> stringResource(R.string.puzzle_rank_amazing)
            PuzzleRanking.Genius -> stringResource(R.string.puzzle_rank_genius)
        }
        Text(text = rankString, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = score.toString())
    }
}

@Composable
fun DiscoveredWordBox(words: Set<String>, defaultExpanded: Boolean = false) {
    var expanded by remember { mutableStateOf(defaultExpanded) }
    OutlinedButton(
        onClick = { expanded = !expanded },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colors.onSurface,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
        ) {
            if (!expanded) {
                val text = if (words.isEmpty()) {
                    stringResource(R.string.puzzle_detail_word_list_empty)
                } else {
                    words.reversed()
                        .joinToString(separator = " ") { word -> word.capitalize(Locale.getDefault()) }
                }
                ChevronRow(text, expanded = false)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ChevronRow(
                        stringResource(
                            R.string.puzzle_detail_word_list_word_count,
                            words.size
                        ), true
                    )
                    if (words.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        ColumnGridList(words.toList())
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnGridList(words: List<String>, columnNum: Int = 3) {
    val wordsPerColumn = ceil(words.size / columnNum.toDouble()).toInt()
    val columns = words.windowed(wordsPerColumn, step = wordsPerColumn, partialWindows = true)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until wordsPerColumn) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (j in 0 until columnNum) {
                        val word = columns.getOrNull(j)?.getOrNull(i) ?: ""
                        println(word)
                        Text(
                            text = word,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

        }
    }
}

@Composable
fun ChevronRow(
    text: String,
    expanded: Boolean,
    textColor: Color = MaterialTheme.colors.onPrimary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            color = textColor
        )
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun PreviewDiscoveredWordBoxEmpty() {
    DiscoveredWordBox(words = emptySet())
}

@Preview
@Composable
fun PreviewDiscoveredWordBoxFull() {
    DiscoveredWordBox(
        words = setOf(
            "handle", "story", "rabbit", "cloud", "couch", "towel", "anger", "greeting"
        )
    )
}

@Preview
@Composable
fun PreviewDiscoveredWordBoxFullExpanded() {
    DiscoveredWordBox(
        words = setOf(
            "handle", "story", "rabbit", "cloud"
        ),
        defaultExpanded = true
    )
}


@Composable
fun InputBox(centerLetter: Char, word: String) {
    val textSize = 30.sp
    val highlightColor = MaterialTheme.colors.primary
    Row {
        Text(
            text = buildAnnotatedString {
                word.forEach { c ->
                    if (c == centerLetter) {
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append(c.toUpperCase())
                        }
                    } else {
                        append(c.toUpperCase())
                    }
                }
            },
            fontSize = textSize,
            fontWeight = FontWeight.Black
        )
        val infiniteTransition = rememberInfiniteTransition()
        val cursorAnimation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Text(
            text = "|",
            fontSize = textSize,
            color = highlightColor,
            fontWeight = FontWeight.Light,
            modifier = Modifier.alpha(if (cursorAnimation >= 0.5f) 1f else 0f)
        )
    }
}

@Composable
fun PuzzleKeypad(centerLetter: Char, outterLetters: List<Char>, onClick: (Char) -> Unit) {
    Layout(
        content = {
            KeypadButton(centerLetter, onClick, primary = true)
            outterLetters.take(6).forEach {
                KeypadButton(it, onClick, primary = false)
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
fun KeypadButton(letter: Char, onClick: (Char) -> Unit, primary: Boolean) {
    Button(
        modifier = Modifier.size(100.dp),
        shape = RegularHexagonalShape(),
        onClick = { onClick.invoke(letter) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (primary) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.secondary
            }
        )
    ) {
        Text(
            text = letter.toUpperCase().toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
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
    KeypadButton(letter = 'x', onClick = {}, primary = true)
}

@Composable
fun ActionBar(onShuffle: () -> Unit, onDelete: () -> Unit, onEnter: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ActionButton(onClick = onDelete) {
            Text(stringResource(R.string.puzzle_detail_actionbar_delete))
        }
        Spacer(Modifier.size(16.dp))
        ActionButton(onClick = onShuffle) {
            Icon(
                Icons.Filled.Autorenew,
                stringResource(R.string.puzzle_detail_actionbar_shuffle)
            )
        }
        Spacer(Modifier.size(16.dp))
        ActionButton(onClick = onEnter) {
            Text(stringResource(R.string.puzzle_detail_actionbar_enter))
        }
    }
}

@Composable
fun ActionButton(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colors.onSurface,
        ),
        shape = RoundedCornerShape(50),
    ) {
        content()
    }
}

@Composable
fun PuzzleDetailLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
    }
}

