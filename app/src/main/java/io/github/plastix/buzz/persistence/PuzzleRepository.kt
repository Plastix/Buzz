package io.github.plastix.buzz.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.PuzzleBoardState
import io.github.plastix.buzz.PuzzleGameState
import io.github.plastix.buzz.blankGameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Encapsulates all operations for storing Puzzle data locally.
 */
class PuzzleRepository @Inject constructor(private val database: PuzzleDatabase) {

    private val dao: PuzzleDao by lazy { database.puzzleDao() }

    fun getPuzzles(): LiveData<List<PuzzleBoardState>> {
        return map(dao.getPuzzles()) { states ->
            states.map { entity ->
                val puzzle = entity.puzzle.toPuzzle()
                PuzzleBoardState(
                    puzzle = puzzle,
                    gameState = entity.gameState?.toGameState() ?: puzzle.blankGameState()
                )
            }
        }
    }

    suspend fun getPuzzle(puzzleId: String): Puzzle? {
        return withContext(Dispatchers.IO) {
            dao.getPuzzleById(puzzleId)?.toPuzzle()
        }
    }

    suspend fun insertPuzzles(puzzles: List<Puzzle>) {
        withContext(Dispatchers.IO) {
            dao.insertPuzzles(puzzles.map(Puzzle::toEntity))
        }
    }

    suspend fun insertGameState(puzzleGameState: PuzzleGameState, puzzleId: String) {
        withContext(Dispatchers.IO) {
            dao.insertGameState(puzzleGameState.toEntity(puzzleId))
        }
    }

    suspend fun getGameState(puzzleId: String): PuzzleGameState? {
        return withContext(Dispatchers.IO) {
            dao.getGameState(puzzleId)?.toGameState()

        }
    }
}