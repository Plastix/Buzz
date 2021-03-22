package io.github.plastix.buzz.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.detail.GameModel

@Dao
interface PuzzleDao {

    @Query("select * from puzzles")
    fun getPuzzles(): LiveData<List<PuzzleEntity>>

    @Query("select * from puzzles where puzzleId == :puzzleId")
    fun getPuzzleById(puzzleId: String): PuzzleEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPuzzles(puzzles: List<PuzzleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGameModel(gameModelEntity: GameModelEntity)

    @Query("select * from `game-states` where puzzleId == :puzzleId")
    fun getGameModel(puzzleId: String): GameModelEntity?
}
