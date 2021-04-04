package io.github.plastix.buzz.persistence.gen

import androidx.room.Dao
import androidx.room.Query
import io.github.plastix.buzz.core.CharacterSet

@Dao
interface DictionaryDao {

    @Query("select * from puzzle_seeds order by random() limit 1")
    fun getRandomPuzzlePuzzleSeed(): PuzzleSeedEntity?

    @Query("select * from dictionary_words where character_set = :set")
    fun getWordsMatchingCharacterSets(set: CharacterSet): List<DictionaryWordEntity>
}