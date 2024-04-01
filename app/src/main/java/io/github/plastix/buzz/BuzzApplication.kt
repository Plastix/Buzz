package io.github.plastix.buzz

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.plastix.buzz.network.PuzzleJobScheduler
import io.github.plastix.buzz.settings.Preferences
import io.github.plastix.buzz.theme.setAppThemeMode
import javax.inject.Inject

@HiltAndroidApp
class BuzzApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var puzzleJobScheduler: PuzzleJobScheduler

    @Inject
    lateinit var preferences: Preferences

    // Wire up Dagger Hilt injection for Work Manager
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setAppTheme()
        puzzleJobScheduler.scheduleDailyDownloadJob()
    }

    private fun setAppTheme() {
        setAppThemeMode(preferences.getTheme())
    }
}
