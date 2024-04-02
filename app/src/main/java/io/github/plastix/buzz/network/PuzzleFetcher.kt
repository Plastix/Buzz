package io.github.plastix.buzz.network

import com.squareup.moshi.Moshi
import io.github.plastix.buzz.Features
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.Result
import io.github.plastix.buzz.thread.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PuzzleFetcher @Inject constructor(
    private val client: OkHttpClient,
    private val json: Moshi,
    @IO private val ioContext: CoroutineContext
) {

    companion object {
        private const val API_URL = "https://www.nytimes.com/puzzles/spelling-bee"

        /**
         * Regex to parse the game data JSON blob out of the HTML page
         */
        @Suppress("RegExpRedundantEscape")
        private val parser = "gameData = (\\{.*?\\}\\})".toRegex()
    }

    /**
     * Fetches the latest two puzzles from the NYTimes "API". This operates on an I/O scheduler
     */
    suspend fun fetchLatestPuzzles(): Result<List<Puzzle>> {
        return if (!Features.PUZZLES_DOWNLOADS_ENABLED) {
            Result.Success(emptyList())
        } else {
            withContext(ioContext) {
                fetchLatestPuzzlesInternal()
            }
        }
    }

    private fun fetchLatestPuzzlesInternal(): Result<List<Puzzle>> {
        try {
            val request = Request.Builder().url(API_URL).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return Result.Error(
                    IllegalArgumentException("Null response body!")
                )
                val payload = parser.find(body)?.groupValues?.get(1) ?: return Result.Error(
                    IllegalArgumentException("Could not parse API response!")
                )
                val adapter = json.adapter(PuzzleContainerResponse::class.java)
                val puzzle: PuzzleContainerResponse =
                    adapter.fromJson(payload) ?: return Result.Error(
                        IllegalArgumentException("Could not deserialize JSON blob! $payload")
                    )

                return Result.Success(
                    listOf(
                        puzzle.today,
                        puzzle.yesterday
                    ).map(PuzzleResponse::toPuzzle)
                )
            } else {
                return Result.Error(IOException("Unexpected code $response"))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}
