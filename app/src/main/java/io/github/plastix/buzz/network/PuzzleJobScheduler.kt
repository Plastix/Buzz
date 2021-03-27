package io.github.plastix.buzz.network

import android.app.Application
import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PuzzleJobScheduler @Inject constructor(
    private val application: Application
) {

    companion object {
        private const val PUZZLE_JOB_ID = "puzzle_download_job"
    }

    fun scheduleDailyDownloadJob() {
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
}