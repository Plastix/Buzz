package io.github.plastix.buzz.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.plastix.buzz.Puzzle

/**
 * Database model for persisting a [Puzzle].
 */
@Entity(tableName = "puzzles")
class PuzzleEntity(
    @PrimaryKey
    val puzzleId: String,
    val centerLetter: Char,
    val outerLetters: Set<Char>,
    val pangrams: Set<String>,
    val answers: Set<String>,
)

fun PuzzleEntity.toPuzzle(): Puzzle {
    return Puzzle(
        date = puzzleId,
        centerLetter = centerLetter,
        outerLetters = outerLetters,
        pangrams = pangrams,
        answers = answers
    )
}

fun List<PuzzleEntity>.toPuzzles(): List<Puzzle> = map(PuzzleEntity::toPuzzle)

fun Puzzle.toEntity(): PuzzleEntity {
    return PuzzleEntity(
        puzzleId = date,
        centerLetter = centerLetter,
        outerLetters = outerLetters,
        pangrams = pangrams,
        answers = answers
    )
}
