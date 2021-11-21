package io.github.plastix.buzz.detail

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import io.github.plastix.buzz.PuzzleRanking
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.BuzzTheme
import io.github.plastix.buzz.theme.LocalUiThemeMode
import io.github.plastix.buzz.util.CustomDialog
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.*

private val LocalViewModel =
    compositionLocalOf<DetailScreen> { error("No click handler set1") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleDetailUi(
    viewModel: PuzzleDetailViewModel,
    onBack: () -> Unit
) {
    CompositionLocalProvider(LocalViewModel provides viewModel) {
        BuzzTheme {
            val debugDialog = rememberSaveable { mutableStateOf(false) }
            Scaffold(topBar = {
                SmallTopAppBar(
                    title = {
                        Text(stringResource(R.string.puzzle_detail_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::infoIconClicked) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.puzzle_detail_toolbar_info)
                            )
                        }

                        val resetClickHandler: () -> Unit =
                            if (viewModel.puzzleResetConfirmationEnabled.observeAsState(true).value)
                                viewModel::resetGame
                            else
                                viewModel::resetConfirmed

                        IconButton(onClick = resetClickHandler) {
                            Icon(
                                imageVector = Icons.Filled.Replay,
                                contentDescription = stringResource(R.string.puzzle_detail_toolbar_reset)
                            )
                        }
                        if (viewModel.showDebugMenu.observeAsState(false).value) {
                            IconButton(onClick = {
                                debugDialog.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.BugReport,
                                    contentDescription = stringResource(R.string.puzzle_detail_debug_tool_title)
                                )
                            }
                        }
                    }
                )
            }) {
                PuzzleDetailScreen()

                if (debugDialog.value) {
                    PuzzleDebugMenu(debugDialog)
                }
            }
        }
    }
}

@Composable
fun PuzzleDebugMenu(debugDialog: MutableState<Boolean>) {
    CustomDialog(
        onDismiss = { debugDialog.value = false },
        title = stringResource(R.string.puzzle_detail_debug_tool_title)
    ) {
        val viewModel: DetailScreen = LocalViewModel.current

        Button(onClick = viewModel::solvePuzzle) {
            Text(
                stringResource(
                    R.string.puzzle_detail_debug_tool_solve_puzzle
                )
            )
        }
    }
}

@Composable
fun PuzzleDetailScreen() {
    val viewModel = LocalViewModel.current
    when (val state =
        viewModel.viewStates.observeAsState(PuzzleDetailViewState.Loading).value) {
        is PuzzleDetailViewState.Loading -> PuzzleDetailLoadingState()
        is PuzzleDetailViewState.Success -> {
            val gameState = state.boardGameState
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                PuzzleBoardLandscape(gameState)
            } else {
                PuzzleBoard(gameState)
            }
            if (gameState.activeDialog != null) {
                ShowDialog(gameState.activeDialog)
            }
        }
        is PuzzleDetailViewState.Error -> PuzzleErrorState()
    }
}

@Composable
fun PuzzleErrorState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = stringResource(R.string.puzzle_detail_error_icon_content_description),
        )
        Spacer(Modifier.size(16.dp))
        Text(
            stringResource(R.string.puzzle_detail_error_description),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PuzzleBoard(
    state: BoardGameViewState,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp)
        ) {
            ScoreBox(state.currentRank, state.currentScore)
            DiscoveredWordBox(
                words = state.discoveredWords,
                pangrams = state.discoveredPangrams,
                state.wordBoxExpanded,
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    WordToastRow(state)
                    InputBox(centerLetter = state.centerLetter, word = state.currentWord)
                }
                PuzzleKeypad(
                    state.centerLetter, state.outerLetters.toList(),
                    modifier = Modifier.fillMaxHeight(0.75f)
                )
                ActionBar()
            }
        }
    }
}

@Composable
fun PuzzleBoardLandscape(state: BoardGameViewState) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScoreBox(state.currentRank, state.currentScore)
            PuzzleKeypad(state.centerLetter, state.outerLetters.toList())
        }
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            DiscoveredWordBox(
                words = state.discoveredWords,
                pangrams = state.discoveredPangrams,
                expanded = state.wordBoxExpanded,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WordToastRow(state)
                InputBox(centerLetter = state.centerLetter, word = state.currentWord)
            }
            ActionBar()
        }
    }
}

@Composable
fun WordToastRow(
    state: BoardGameViewState,
    durationMs: Long = 1000
) {
    val viewModel = LocalViewModel.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (state.activeWordToast != null) 1f else 0f),
        horizontalArrangement = Arrangement.Center
    ) {
        val message = when (val toast = state.activeWordToast) {
            is WordToast.Success -> "+${toast.pointValue}"
            is WordToast.Error -> stringResource(id = toast.wordError.errorMessage)
            null -> ""
        }
        Text(message)
    }
    LaunchedEffect(state.activeWordToast) {
        delay(durationMs)
        viewModel.dismissActiveToast()
    }
}

@Composable
fun ShowDialog(activeDialog: Dialog) {
    when (activeDialog) {
        is Dialog.ConfirmReset -> ShowResetConfirmationDialog()
        is Dialog.InfoDialog -> InfoDialog()
        is Dialog.RankingDialog -> RankingDialog(activeDialog.maxPuzzleScore)
    }
}


@Composable
fun InfoDialog() {
    val viewModel = LocalViewModel.current
    CustomDialog(
        onDismiss = viewModel::dismissActiveDialog,
        title = stringResource(R.string.puzzle_rules_dialog_title)
    ) {
        Text(
            text = stringResource(R.string.puzzle_rules_title),
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(16.dp))
        BulletPointList(stringArrayResource(R.array.puzzle_rules))
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(R.string.puzzle_scoring_rules_title),
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(16.dp))
        BulletPointList(stringArrayResource(R.array.puzzle_scoring_rules))
    }
}

@Composable
fun RankingDialog(maxPuzzleScore: Int) {
    val viewModel = LocalViewModel.current
    CustomDialog(
        onDismiss = viewModel::dismissActiveDialog,
        title = stringResource(R.string.puzzle_ranking_dialog_title)
    ) {
        Text(
            text = stringResource(R.string.puzzle_ranking_description),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(16.dp))
        PuzzleRanking.sortedValues.minus(PuzzleRanking.QueenBee).forEach { ranking ->
            val rankName = stringResource(ranking.displayString)
            val rankScore = (maxPuzzleScore * (ranking.percentCutoff / 100.0)).roundToInt()
            Text(text = "$rankName ($rankScore)")
        }
    }
}

@Composable
fun BulletPointList(strings: Array<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        strings.forEach { string ->
            Row(verticalAlignment = Alignment.Top) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        modifier = Modifier.size(6.dp),
                        imageVector = Icons.Filled.Circle, contentDescription = "bullet point"
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = string,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBulletPointList() {
    ProvideFakeViewModel {
        Box(modifier = Modifier.background(Color.White)) {
            BulletPointList(stringArrayResource(id = R.array.puzzle_rules))
        }
    }
}

@Composable
fun ShowResetConfirmationDialog() {
    val viewModel = LocalViewModel.current
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
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun ScoreBox(rank: PuzzleRanking, score: Int) {
    val viewModel = LocalViewModel.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.scoreBarClicked() }
            .padding(bottom = 12.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MaxWidthText(
            text = stringResource(rank.displayString),
            options = PuzzleRanking.sortedValues.map {
                stringResource(it.displayString)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val sortedRanks = PuzzleRanking.sortedValues
                val indexOfRank = sortedRanks.indexOf(rank)
                sortedRanks.forEachIndexed { index, puzzleRanking ->
                    val bubbleSize = if (puzzleRanking == rank) 24.dp else 8.dp
                    val color =
                        if (index <= indexOfRank) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .defaultMinSize(minWidth = bubbleSize, minHeight = bubbleSize),
                        shape = CircleShape,
                        color = color,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (puzzleRanking == rank) {
                                Text(
                                    text = score.toString(),
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaxWidthText(
    text: String,
    options: List<String> = emptyList(),
    fontWeight: FontWeight = FontWeight.Bold
) {
    Layout(
        content = {
            setOf(text).plus(options).forEach { string ->
                Text(string, fontWeight = fontWeight)
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minHeight = 0))
        }
        val maxWidth: Int = placeables.maxOf(Placeable::width)
        layout(maxWidth, constraints.minHeight) {
            val item = placeables.first()
            item.place(0, -item.height / 2)
        }
    }
}

@Composable
fun DiscoveredWordBox(
    words: Set<String>,
    pangrams: Set<String>,
    expanded: Boolean = false,
) {
    val viewModel = LocalViewModel.current
    OutlinedButton(
        onClick = viewModel::toggleWorldBox,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
        ) {
            if (!expanded) {
                val text: AnnotatedString = if (words.isEmpty()) {
                    AnnotatedString(stringResource(R.string.puzzle_detail_word_list_empty))
                } else {
                    buildAnnotatedString {
                        words.reversed().forEachIndexed { index, word ->
                            val fontWeight =
                                if (word in pangrams) FontWeight.ExtraBold else FontWeight.Normal
                            withStyle(style = SpanStyle(fontWeight = fontWeight)) {
                                append(word.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.ENGLISH
                                    ) else it.toString()
                                })
                            }

                            if (index < words.size - 1) {
                                append(" ")
                            }
                        }
                    }
                }
                ChevronRow(text, expanded = false)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ChevronRow(
                        AnnotatedString(
                            stringResource(
                                R.string.puzzle_detail_word_list_word_count,
                                words.size
                            )
                        ), true
                    )
                    if (words.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        ColumnGridList(words.toList(), pangrams)
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnGridList(words: List<String>, pangrams: Set<String>, columnNum: Int = 3) {
    val rows = words.sorted().chunked(columnNum)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(rows) { rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0 until columnNum) {
                    val word = rowWords.getOrNull(i) ?: ""
                    Text(
                        text = word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() },
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (word in pangrams) FontWeight.ExtraBold else FontWeight.Normal
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun ChevronRow(
    text: AnnotatedString,
    expanded: Boolean,
    textColor: Color = MaterialTheme.colorScheme.onSurface
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
    ProvideFakeViewModel {
        DiscoveredWordBox(words = emptySet(), pangrams = emptySet(), false)
    }
}

@Preview
@Composable
fun PreviewDiscoveredWordBoxFull() {
    ProvideFakeViewModel {
        DiscoveredWordBox(
            words = setOf(
                "handle", "story", "rabbit", "cloud", "couch", "towel", "anger", "greeting"
            ),
            pangrams = emptySet()
        )
    }
}

@Preview
@Composable
fun PreviewDiscoveredWordBoxFullExpanded() {
    ProvideFakeViewModel {
        DiscoveredWordBox(
            words = setOf(
                "handle", "story", "rabbit", "cloud"
            ),
            pangrams = emptySet(),
            expanded = true
        )
    }
}


@Composable
fun InputBox(centerLetter: Char, word: String) {
    val textSize = 30.sp
    val highlightColor = MaterialTheme.colorScheme.primary
    Row {
        Text(
            text = buildAnnotatedString {
                word.forEach { c ->
                    if (c == centerLetter) {
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append(c.uppercaseChar())
                        }
                    } else {
                        append(c.uppercaseChar())
                    }
                }
            },
            fontSize = textSize,
            fontWeight = FontWeight.Black,
            maxLines = 1,
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
fun PuzzleKeypad(
    centerLetter: Char,
    outerLetters: List<Char>,
    modifier: Modifier = Modifier
) {
    Layout(
        content = {
            KeypadButton(centerLetter, primary = true)
            outerLetters.take(6).forEach {
                KeypadButton(it, primary = false)
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            val buttonSize = min(constraints.maxWidth, constraints.maxHeight) / 3
            measurable.measure(
                Constraints(
                    maxWidth = buttonSize,
                    maxHeight = buttonSize
                )
            )
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
    ProvideFakeViewModel {
        PuzzleKeypad(
            centerLetter = 'x',
            outerLetters = listOf('a', 'b', 'c', 'd', 'e', 'f')
        )
    }
}


@Composable
fun KeypadButton(letter: Char, primary: Boolean) {
    val viewModel = LocalViewModel.current
    Button(
        modifier = Modifier.fillMaxSize(),
        shape = RegularHexagonalShape(),
        onClick = { viewModel.keypress(letter) },
        colors = if (LocalUiThemeMode.current.isDarkMode().not()) {
            ButtonDefaults.buttonColors(
                containerColor = if (primary) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )
        } else {
            ButtonDefaults.outlinedButtonColors(
                contentColor = if (primary) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        border = if (LocalUiThemeMode.current.isDarkMode().not()) null else ButtonDefaults.outlinedButtonBorder
    ) {
        BoxWithConstraints(contentAlignment = Alignment.Center) {
            val fontSize = with(LocalDensity.current) {
                (maxWidth * 0.15f).toPx().sp
            }
            Text(
                text = letter.uppercaseChar().toString(),
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold
            )
        }
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
        // Loop 7 times so that we connect all 6 edges together with lines
        for (i in 0 until 7) {
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
    ProvideFakeViewModel {
        KeypadButton(letter = 'x', primary = true)
    }
}

@Composable
fun ActionBar() {
    val viewModel = LocalViewModel.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        ActionButton(onClick = viewModel::delete) {
            Text(
                stringResource(R.string.puzzle_detail_actionbar_delete),
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1
            )
        }
        Spacer(Modifier.size(16.dp))
        ActionButton(
            onClick = viewModel::shuffle,
            shape = CircleShape
        ) {
            Icon(
                Icons.Filled.Autorenew,
                stringResource(R.string.puzzle_detail_actionbar_shuffle)
            )
        }
        Spacer(Modifier.size(16.dp))
        ActionButton(onClick = viewModel::enter) {
            Text(
                stringResource(R.string.puzzle_detail_actionbar_enter),
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(50),
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = shape,
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
//        CircularProgressIndicator(modifier = Modifier.size(100.dp))
    }
}

@Composable
private fun ProvideFakeViewModel(block: @Composable () -> Unit) {
    CompositionLocalProvider(LocalViewModel provides EmptyDetailScreen()) {
        block.invoke()
    }
}

