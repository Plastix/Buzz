package io.github.plastix.buzz.network

import com.squareup.moshi.Moshi
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import javax.inject.Inject

class PuzzleFetcher @Inject constructor(
    private val client: OkHttpClient,
    private val json: Moshi
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
        return withContext(Dispatchers.IO) {
            fetchLatestPuzzlesInternal()
        }
    }

    private fun fetchLatestPuzzlesInternal(): Result<List<Puzzle>> {
        val request = Request.Builder().url(API_URL).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            try {
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
            } catch (e: Exception) {
                return Result.Error(e)
            }
        } else {
            return Result.Error(IOException("Unexpected code $response"))
        }
    }
}
