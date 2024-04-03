package io.github.plastix.buzz.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SwipeDismiss(
    modifier: Modifier = Modifier,
    item: T,
    background: @Composable (isDismissed: Boolean) -> Unit,
    content: @Composable (isDismissed: Boolean) -> Unit,
    directions: Set<DismissDirection> = setOf(DismissDirection.StartToEnd),
    exitDurationMs: Int = 200,
    onDismiss: (T) -> Unit
) {
    val dismissState = remember(item) {
        DismissState(DismissValue.Default)
    }
    val isDismissed = dismissState.isDismissed(DismissDirection.StartToEnd)
    if (isDismissed) {
        LaunchedEffect(item) {
            // A little bit of a hack to invoke our dismissed callback after the animation finishes
            delay(exitDurationMs.toLong())
            onDismiss.invoke(item)
        }
    }
    AnimatedVisibility(
        modifier = modifier,
        visible = !isDismissed,
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = exitDurationMs,
            )
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            modifier = modifier,
            state = dismissState,
            directions = directions,
            background = { background(isDismissed) },
            dismissContent = { content(isDismissed) }
        )
    }
}