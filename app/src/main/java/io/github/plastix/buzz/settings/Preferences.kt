package io.github.plastix.buzz.settings

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.LiveData
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

    val debugToolsEnabled: LiveData<Boolean> by lazy {
        BooleanSharedPreferenceLiveData(
            preferenceManager,
            resources.getString(R.string.preferences_debug_tools_enabled)
        )
    }

    fun toggleDevMenuEnabled(): Boolean {
        val oldValue = preferenceManager.getBoolean(
            resources.getString(R.string.preferences_debug_tools_enabled),
            false
        )
        val newValue = !oldValue
        preferenceManager.edit {
            putBoolean(resources.getString(R.string.preferences_debug_tools_enabled), newValue)
        }

        return newValue
    }

    val newPuzzleConfirmationEnabled: LiveData<Boolean> by lazy {
        BooleanSharedPreferenceLiveData(
            preferenceManager,
            resources.getString(R.string.preferences_action_confirmations_new_puzzle)
        )
    }

    val resetPuzzleConfirmationEnabled: LiveData<Boolean> by lazy {
        BooleanSharedPreferenceLiveData(
            preferenceManager,
            resources.getString(R.string.preferences_action_confirmations_reset_puzzle),
            true
        )
    }
}
