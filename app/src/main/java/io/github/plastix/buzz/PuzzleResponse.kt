package io.github.plastix.buzz

import com.squareup.moshi.JsonClass

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
