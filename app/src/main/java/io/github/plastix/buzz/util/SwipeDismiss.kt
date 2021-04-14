package io.github.plastix.buzz.util

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

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
    val dismissState = remember(item) {
        DismissState(DismissValue.Default)
    }
    val isDismissed = dismissState.isDismissed(DismissDirection.StartToEnd)
    if (isDismissed) {
        onDismiss.invoke(item)
    }
    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = directions,
        background = { background(isDismissed) },
        dismissContent = { content(isDismissed) }
    )
}