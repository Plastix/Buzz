package io.github.plastix.buzz.network

import com.squareup.moshi.JsonClass
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.PuzzleType
import io.github.plastix.buzz.util.parseDate

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
        id = Puzzle.AUTO_GENERATE_ID,
        date = printDate.parseDate(),
        centerLetter = centerLetter[0],
        outerLetters = outerLetters.map { it[0] }.toSet(),
        pangrams = pangrams.toSet(),
        answers = answers.toSet(),
        type = PuzzleType.DOWNLOADED
    )
}
