package io.github.plastix.buzz.persistence

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.github.plastix.buzz.Puzzle

/**
 * Database model for persisting a [Puzzle].
 */
@Entity(tableName = "puzzles")
class PuzzleEntity(
    @PrimaryKey
    val date: String,
    val centerLetter: Char,
    @Ignore // TODO Add real type adapters for these
    val outerLetters: Set<Char>,
    @Ignore
    val pangrams: Set<String>,
    @Ignore
    val answers: Set<String>,
) {
    constructor(date: String, centerLetter: Char) : this(
        date,
        centerLetter,
        emptySet(),
        emptySet(),
        emptySet()
    )
}

fun PuzzleEntity.toPuzzle(): Puzzle {
    return Puzzle(
        date = date,
        centerLetter = centerLetter,
        outerLetters = outerLetters,
        pangrams = pangrams,
        answers = answers
    )
}

fun List<PuzzleEntity>.toPuzzles(): List<Puzzle> = map(PuzzleEntity::toPuzzle)

fun Puzzle.toEntity(): PuzzleEntity {
    return PuzzleEntity(
        date = date,
        centerLetter = centerLetter,
        outerLetters = outerLetters,
        pangrams = pangrams,
        answers = answers
    )
}

fun List<Puzzle>.toEntities(): List<PuzzleEntity> = map(Puzzle::toEntity)

