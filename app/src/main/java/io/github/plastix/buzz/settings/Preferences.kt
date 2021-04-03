package io.github.plastix.buzz.settings

import android.app.Application
import androidx.preference.PreferenceManager
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.ThemeMode
import javax.inject.Inject

class Preferences @Inject constructor(
    private val app: Application
) {
    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(app)
    private val resources = app.resources

    fun getTheme(): ThemeMode {
        val key = resources.getString(R.string.preferences_appearance_theme)
        val themeString = preferenceManager.getString(key, null)
        return ThemeMode.fromPersistenceKey(app, themeString)
    }

    fun autoDownloadEnabled(): Boolean {
        return preferenceManager.getBoolean(
            resources.getString(R.string.preferences_download_enabled),
            true
        )
    }
}