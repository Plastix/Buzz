package io.github.plastix.buzz

import kotlin.math.roundToInt

/**
 * A combination of a Puzzle and a Puzzle Game State. With this information, we can calculate
 * the user's current score and puzzle ranking.
 */
data class PuzzleBoardState(
    val puzzle: Puzzle,
    val gameState: PuzzleGameState
) {
    val currentScore: Int = gameState.discoveredWords.sumOf { word -> puzzle.scoreWord(word) }
    private val currentPercent: Int =
        ((currentScore / puzzle.maxScore.toDouble()) * 100).roundToInt()
    val currentRank: PuzzleRanking = PuzzleRanking.entries
        .filter { rank -> rank.percentCutoff <= currentPercent }
        .maxByOrNull { rank -> rank.percentCutoff } ?: PuzzleRanking.Beginner


    fun validateWord(word: String): WordResult {
        // The order of this when statement is the order that these errors should take precedence
        return when {
            word.length < Puzzle.MIN_WORD_LENGTH -> WordResult.Error(WordError.TooShort)
            word.length > Puzzle.MAX_WORD_LENGTH -> WordResult.Error(WordError.TooLong)
            !word.contains(puzzle.centerLetter) -> WordResult.Error(WordError.MissingCenterLetter)
            word in gameState.discoveredWords -> WordResult.Error(WordError.AlreadyFound)
            word !in puzzle.answers -> WordResult.Error(WordError.NotInWordList)
            else -> WordResult.Valid
        }
    }
}
