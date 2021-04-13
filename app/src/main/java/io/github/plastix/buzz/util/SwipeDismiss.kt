package io.github.plastix.buzz.util

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun <T> SwipeDismiss(
    modifier: Modifier = Modifier,
    item: T,
    background: @Composable (isDismissed: Boolean) -> Unit,
    content: @Composable (isDismissed: Boolean) -> Unit,
    directions: Set<DismissDirection> = setOf(DismissDirection.StartToEnd),
    onDismiss: (T) -> Unit
) {
    val dismissState = rememberDismissState()
    val isDismissed = directions.any { dismissState.isDismissed(it) }
    if (isDismissed) {
        LaunchedEffect(null) {
            onDismiss.invoke(item)
            dismissState.reset()
        }
    }
    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = directions,
        background = { background(isDismissed) },
        dismissContent = { content(isDismissed) }
    )
}