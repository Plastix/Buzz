package io.github.plastix.buzz.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.PuzzleBoardState
import io.github.plastix.buzz.PuzzleGameState
import io.github.plastix.buzz.PuzzleType
import io.github.plastix.buzz.blankGameState
import io.github.plastix.buzz.core.Constants
import io.github.plastix.buzz.core.powerSet
import io.github.plastix.buzz.core.size
import io.github.plastix.buzz.core.toCharArray
import io.github.plastix.buzz.core.toCharacterSet
import io.github.plastix.buzz.persistence.gen.DictionaryDao
import io.github.plastix.buzz.persistence.gen.DictionaryDatabase
import io.github.plastix.buzz.thread.IO
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Encapsulates all operations for storing Puzzle data locally.
 */
class PuzzleRepository @Inject constructor(
    private val database: PuzzleDatabase,
    private val dictionaryDatabase: DictionaryDatabase,
    @IO private val ioContext: CoroutineContext
) {

    private val dao: PuzzleDao by lazy { database.puzzleDao() }
    private val dictionary: DictionaryDao by lazy { dictionaryDatabase.dictionaryDao() }

    fun getPuzzles(): LiveData<List<PuzzleBoardState>> {
        return dao.getPuzzles().map { states ->
            states.map { entity ->
                val puzzle = entity.puzzle.toPuzzle()
                PuzzleBoardState(
                    puzzle = puzzle,
                    gameState = entity.gameState?.toGameState() ?: puzzle.blankGameState()
                )
            }
        }
    }

    suspend fun getPuzzle(puzzleId: Long): Puzzle? {
        return withContext(ioContext) {
            dao.getPuzzleById(puzzleId)?.toPuzzle()
        }
    }

    suspend fun insertPuzzles(puzzles: List<Puzzle>) {
        withContext(ioContext) {
            dao.insertPuzzles(puzzles.map(Puzzle::toEntity))
        }
    }

    suspend fun insertGameState(puzzleGameState: PuzzleGameState, puzzleId: Long) {
        withContext(ioContext) {
            dao.insertGameState(puzzleGameState.toEntity(puzzleId))
        }
    }

    suspend fun getGameState(puzzleId: Long): PuzzleGameState? {
        return withContext(ioContext) {
            dao.getGameState(puzzleId)?.toGameState()
        }
    }

    suspend fun generateRandomPuzzle() {
        return withContext(ioContext) {
            val charSet = dictionary.getRandomPuzzlePuzzleSeed()?.characterSet
                ?: error("Failed to find valid puzzle character set!")
            val requiredCharSet = charSet.toCharArray().random().toCharacterSet()
            val keys = charSet.powerSet().map { it or requiredCharSet }.toSet()
            val solutions = keys.flatMap {
                dictionary.getWordsMatchingCharacterSets(it)
            }
            assert(solutions.isNotEmpty()) {
                "No solutions to puzzle!"
            }
            val pangrams = solutions.filter { it.characterSet.size >= Constants.LETTER_COUNT }
            assert(pangrams.isNotEmpty()) {
                "No pangram in puzzle!"
            }

            val newPuzzle = PuzzleEntity(
                puzzleId = Puzzle.AUTO_GENERATE_ID,
                date = LocalDateTime.now(),
                centerLetter = requiredCharSet.toCharArray().first(),
                outerLetters = (requiredCharSet xor charSet).toCharArray().toSet(),
                pangrams = pangrams.map { it.word }.toSet(),
                answers = solutions.map { it.word }.toSet(),
                puzzleType = PuzzleType.GENERATED
            )
            dao.insertPuzzles(listOf(newPuzzle))
        }
    }

    suspend fun deletePuzzleById(puzzleId: Long) {
        withContext(ioContext) {
            dao.deleteByPuzzleId(puzzleId)
        }
    }
}