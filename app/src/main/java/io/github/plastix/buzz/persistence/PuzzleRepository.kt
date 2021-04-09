package io.github.plastix.buzz.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import io.github.plastix.buzz.*
import io.github.plastix.buzz.core.*
import io.github.plastix.buzz.persistence.gen.DictionaryDao
import io.github.plastix.buzz.persistence.gen.DictionaryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

/**
 * Encapsulates all operations for storing Puzzle data locally.
 */
class PuzzleRepository @Inject constructor(
    private val database: PuzzleDatabase,
    private val dictionaryDatabase: DictionaryDatabase
) {

    private val dao: PuzzleDao by lazy { database.puzzleDao() }
    private val dictionary: DictionaryDao by lazy { dictionaryDatabase.dictionaryDao() }

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

    suspend fun getPuzzle(puzzleId: Long): Puzzle? {
        return withContext(Dispatchers.IO) {
            dao.getPuzzleById(puzzleId)?.toPuzzle()
        }
    }

    suspend fun insertPuzzles(puzzles: List<Puzzle>) {
        withContext(Dispatchers.IO) {
            dao.insertPuzzles(puzzles.map(Puzzle::toEntity))
        }
    }

    suspend fun insertGameState(puzzleGameState: PuzzleGameState, puzzleId: Long) {
        withContext(Dispatchers.IO) {
            dao.insertGameState(puzzleGameState.toEntity(puzzleId))
        }
    }

    suspend fun getGameState(puzzleId: Long): PuzzleGameState? {
        return withContext(Dispatchers.IO) {
            dao.getGameState(puzzleId)?.toGameState()

        }
    }

    suspend fun generateRandomPuzzle() {
        return withContext(Dispatchers.IO) {
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
                date = LocalDate.now(),
                centerLetter = requiredCharSet.toCharArray().first(),
                outerLetters = (requiredCharSet xor charSet).toCharArray().toSet(),
                pangrams = pangrams.map { it.word }.toSet(),
                answers = solutions.map { it.word }.toSet(),
                puzzleType = PuzzleType.GENERATED
            )
            dao.insertPuzzles(listOf(newPuzzle))
        }
    }
}