package io.github.plastix.buzz

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.plastix.buzz.network.PuzzleJobScheduler
import io.github.plastix.buzz.theme.ThemeMode
import io.github.plastix.buzz.theme.setAppThemeMode
import javax.inject.Inject

@HiltAndroidApp
class BuzzApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var puzzleJobScheduler: PuzzleJobScheduler

    // Wire up Dagger Hilt injection for Work Manager
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        setupDarkMode()
        puzzleJobScheduler.scheduleDailyDownloadJob()
    }

    private fun setupDarkMode() {
        val themeString = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.preferences_appearance_theme), null)

        setAppThemeMode(ThemeMode.fromPersistenceKey(this, themeString))
    }
}
