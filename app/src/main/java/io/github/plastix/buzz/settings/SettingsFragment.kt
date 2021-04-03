package io.github.plastix.buzz.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import io.github.plastix.buzz.R
import io.github.plastix.buzz.theme.ThemeMode
import io.github.plastix.buzz.theme.setAppThemeMode

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_settings, rootKey)

        findPreference<ListPreference>(getString(R.string.preferences_appearance_theme))?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue is String) {
                val mode = ThemeMode.fromPersistenceKey(requireContext(), newValue)
                setAppThemeMode(mode)
            }

            true
        }
    }
}