package io.github.plastix.buzz

/**
 * A combination of a Puzzle and a Puzzle Game State. With this information, we can calculate
 * the user's current score and puzzle ranking.
 */
data class PuzzleBoardState(
    val puzzle: Puzzle,
    val gameState: PuzzleGameState
) {
    val currentScore: Int = gameState.discoveredWords.sumBy { word -> puzzle.scoreWord(word) }
    private val currentPercent: Int =
        ((currentScore / puzzle.maxScore.toDouble()) * 100).toInt()
    val currentRank: PuzzleRanking = PuzzleRanking.values()
        .filter { rank -> rank.percentCutoff <= currentPercent }
        .maxByOrNull { rank -> rank.percentCutoff } ?: PuzzleRanking.Beginner
}