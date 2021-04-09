package io.github.plastix.buzz.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PuzzleDao {

    @Query("select * from puzzles order by puzzleId desc")
    fun getPuzzles(): LiveData<List<PuzzleEntityWithGameState>>

    @Query("select * from puzzles where puzzleId == :puzzleId")
    fun getPuzzleById(puzzleId: Long): PuzzleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPuzzles(puzzles: List<PuzzleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGameState(puzzleGameState: PuzzleGameStateEntity)

    @Query("select * from `game-states` where puzzleId == :puzzleId")
    fun getGameState(puzzleId: Long): PuzzleGameStateEntity?
}
