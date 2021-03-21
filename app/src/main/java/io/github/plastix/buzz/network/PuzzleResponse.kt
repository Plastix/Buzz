package io.github.plastix.buzz.network

import com.squareup.moshi.JsonClass
import io.github.plastix.buzz.Puzzle

/**
 * Corresponds to the JSON blob returned by the NYTimes "API"
 */
@JsonClass(generateAdapter = true)
data class PuzzleContainerResponse(
    val today: PuzzleResponse,
    val yesterday: PuzzleResponse
)

@JsonClass(generateAdapter = true)
data class PuzzleResponse(
    val id: Long,
    val printDate: String,
    val centerLetter: String,
    val outerLetters: List<String>,
    val pangrams: List<String>,
    val answers: List<String>,
    val editor: String
)

/**
 * Converts a JSON response to an internal client model.
 */
fun PuzzleResponse.toPuzzle(): Puzzle {
    return Puzzle(
        date = printDate,
        centerLetter = centerLetter[0],
        outerLetters = outerLetters.map { it[0] }.toSet(),
        pangrams = pangrams.toSet(),
        answers = answers.toSet()
    )
}
