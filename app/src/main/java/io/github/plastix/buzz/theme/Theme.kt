package io.github.plastix.buzz.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
    val darkTheme = themeMode.isDarkMode()
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(LocalContext.current)
            } else {
                dynamicLightColorScheme(LocalContext.current)
            }
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
