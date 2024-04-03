package io.github.plastix.buzz.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.plastix.buzz.R

enum class ThemeMode(val persistenceKey: Int) {
    ALWAYS_LIGHT(R.string.preferences_appearance_theme_always_light),
    ALWAYS_DARK(R.string.preferences_appearance_theme_always_dark),
    AUTO(R.string.preferences_appearance_theme_auto);

    @Composable
    fun isDarkMode(): Boolean {
        return when (this) {
            ALWAYS_LIGHT -> false
            ALWAYS_DARK -> true
            AUTO -> isSystemInDarkTheme()
        }
    }

    companion object {
        fun fromPersistenceKey(context: Context, string: String?): ThemeMode {
            return entries.firstOrNull { context.getString(it.persistenceKey) == string } ?: AUTO
        }
    }
}

fun setAppThemeMode(themeMode: ThemeMode) {
    LocalUiThemeMode.provides(themeMode)

    // Our preferences use non-compose UI so we need to also set appcompat dark mode settings
    val appCompatMode = when (themeMode) {
        ThemeMode.ALWAYS_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        ThemeMode.ALWAYS_DARK -> AppCompatDelegate.MODE_NIGHT_YES
        ThemeMode.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(appCompatMode)
}

val LocalUiThemeMode =
    staticCompositionLocalOf { ThemeMode.AUTO }