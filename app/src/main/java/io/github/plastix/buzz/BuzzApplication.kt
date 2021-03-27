package io.github.plastix.buzz

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.plastix.buzz.network.PuzzleJobScheduler
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
        puzzleJobScheduler.scheduleDailyDownloadJob()
    }
}
