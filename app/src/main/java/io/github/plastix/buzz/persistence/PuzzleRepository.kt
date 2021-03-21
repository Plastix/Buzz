package io.github.plastix.buzz.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import io.github.plastix.buzz.Puzzle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Encapsulates all operations for storing Puzzle data locally.
 */
class PuzzleRepository(private val database: PuzzleDatabase) {

    private val dao: PuzzleDao by lazy { database.puzzleDao() }

    fun getPuzzles(): LiveData<List<Puzzle>> {
        return map(dao.getPuzzles(), List<PuzzleEntity>::toPuzzles)
    }

    suspend fun insertPuzzles(puzzles: List<Puzzle>) {
        withContext(Dispatchers.IO) {
            dao.insertPuzzles(puzzles.map(Puzzle::toEntity))
        }
    }
}