package io.github.plastix.buzz.persistence.gen

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.plastix.buzz.core.CharacterSet

/**
 * Do not change this schema blindly. It is set up to match the database preprocessor in
 * :dataset
 */
@Entity(
    tableName = "dictionary_words", indices = [Index(
        name = "character_set_index",
        unique = false,
        value = arrayOf("character_set")
    )]
)
data class DictionaryWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "character_set")
    val characterSet: CharacterSet,
    val word: String
)