package io.github.plastix.buzz.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val YELLOW = Color(0xfff8cd05)
private val GRAY = Color(0xffe6e6e6)
private val YELLOW_NIGHT = Color(0xFFE4CD05)
private val GRAY_NIGHT = Color(0xFF6F6F6F)
private val ERROR = Color(0xFFCF6679)

private val DarkColors = darkColorScheme(
    primary = YELLOW_NIGHT,
    secondary = GRAY_NIGHT,
    error = ERROR
)
private val LightColors = lightColorScheme(
    primary = YELLOW,
    secondary = GRAY,
    onSurface = Color.Black,
    onPrimary = Color.Black,
    error = ERROR
)

@Composable
fun BuzzTheme(
    content: @Composable () -> Unit
) {
    val themeMode = LocalUiThemeMode.current
    MaterialTheme(
        colorScheme = if (themeMode.isDarkMode()) DarkColors else LightColors,
        content = content
    )
}
