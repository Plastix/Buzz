package io.github.plastix.buzz.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val YELLOW = Color(0xfff8cd05)
private val GRAY = Color(0xffe6e6e6)


// TODO Dark mode colors
private val DarkColors = darkColors(
    primary = YELLOW,
    secondary = GRAY,
)
private val LightColors = lightColors(
    primary = YELLOW,
    secondary = GRAY,
)

@Composable
fun BuzzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
