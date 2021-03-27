package io.github.plastix.buzz.network

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.plastix.buzz.persistence.PuzzleRepository

@HiltWorker
class PuzzleDownloadJob @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val fetcher: PuzzleFetcher,
    private val repository: PuzzleRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            when (val result = fetcher.fetchLatestPuzzles()) {
                is io.github.plastix.buzz.Result.Error -> Result.retry()
                is io.github.plastix.buzz.Result.Success -> {
                    repository.insertPuzzles(result.data)
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}