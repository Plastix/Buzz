package io.github.plastix.buzz.persistence.gen

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.plastix.buzz.core.CharacterSet

/**
 * Represents a character set which can be used as the set of characters in a puzzle. The
 * character set is guaranteed to have exactly 7 characters.
 *
 * Do not change this schema blindly. It is set up to match the database preprocessor in
 * :dataset
 */
@Entity(tableName = "puzzle_seeds")
class PuzzleSeedEntity(
    @PrimaryKey
    @ColumnInfo(name = "character_set")
    val characterSet: CharacterSet
)