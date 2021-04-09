package io.github.plastix.buzz.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.PuzzleType
import java.time.LocalDate
import java.util.*

/**
 * Database model for persisting a [Puzzle].
 */
@Entity(tableName = "puzzles")
class PuzzleEntity(
    @PrimaryKey(autoGenerate = true)
    val puzzleId: Long,
    val date: LocalDate,
    val centerLetter: Char,
    val outerLetters: Set<Char>,
    val pangrams: Set<String>,
    val answers: Set<String>,
    val puzzleType: PuzzleType
)

fun PuzzleEntity.toPuzzle(): Puzzle {
    return Puzzle(
        id = puzzleId,
        date = date,
        centerLetter = centerLetter,
        outerLetters = outerLetters,
        pangrams = pangrams,
        answers = answers,
        type = puzzleType
    )
}

fun List<PuzzleEntity>.toPuzzles(): List<Puzzle> = map(PuzzleEntity::toPuzzle)

fun Puzzle.toEntity(): PuzzleEntity {
    return PuzzleEntity(
        puzzleId = id,
        date = date,
        centerLetter = centerLetter,
        outerLetters = outerLetters,
        pangrams = pangrams,
        answers = answers,
        puzzleType = type
    )
}
