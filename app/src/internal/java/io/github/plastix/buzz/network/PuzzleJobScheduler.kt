package io.github.plastix.buzz.network

import android.app.Application
import androidx.work.*
import io.github.plastix.buzz.settings.Preferences
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PuzzleJobScheduler @Inject constructor(
    private val application: Application,
    private val preferences: Preferences
) {

    companion object {
        private const val PUZZLE_JOB_ID = "puzzle_download_job"
    }

    fun scheduleDailyDownloadJob() {
        if (preferences.autoDownloadEnabled()) {
            scheduleJob()
        } else {
            clearJob()
        }
    }

    private fun scheduleJob() {
        // Currently does not support setting a specific time to run each day
        val workRequest = PeriodicWorkRequest.Builder(
            PuzzleDownloadJob::class.java,
            1L,
            TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        WorkManager.getInstance(application)
            .enqueueUniquePeriodicWork(
                PUZZLE_JOB_ID,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    private fun clearJob() {
        WorkManager.getInstance(application)
            .cancelUniqueWork(PUZZLE_JOB_ID)
    }
}