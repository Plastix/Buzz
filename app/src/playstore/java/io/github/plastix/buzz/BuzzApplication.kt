package io.github.plastix.buzz

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.plastix.buzz.settings.Preferences
import io.github.plastix.buzz.theme.setAppThemeMode
import javax.inject.Inject

@HiltAndroidApp
class BuzzApplication : Application() {

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate() {
        super.onCreate()
        setAppTheme()
    }

    private fun setAppTheme() {
        setAppThemeMode(preferences.getTheme())
    }
}
