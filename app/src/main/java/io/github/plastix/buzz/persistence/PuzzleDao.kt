package io.github.plastix.buzz.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.plastix.buzz.Puzzle

@Dao
interface PuzzleDao {

    @Query("select * from puzzles")
    fun getPuzzles(): LiveData<List<PuzzleEntity>>

    @Query("select * from puzzles where date == :puzzleId")
    fun getPuzzleById(puzzleId: String): LiveData<PuzzleEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPuzzles(puzzles: List<PuzzleEntity>)
}
