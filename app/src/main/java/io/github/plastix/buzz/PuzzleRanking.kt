package io.github.plastix.buzz

import androidx.annotation.StringRes

enum class PuzzleRanking(
    val percentCutoff: Int,
    @StringRes
    val displayString: Int
) {
    Beginner(
        percentCutoff = 0,
        displayString = R.string.puzzle_rank_beginner
    ),
    GoodStart(
        percentCutoff = 2,
        displayString = R.string.puzzle_rank_goodstart
    ),
    MovingUp(
        percentCutoff = 5,
        displayString = R.string.puzzle_rank_movingup
    ),
    Good(
        percentCutoff = 8,
        displayString = R.string.puzzle_rank_good
    ),
    Solid(
        percentCutoff = 15,
        displayString = R.string.puzzle_rank_solid
    ),
    Nice(
        percentCutoff = 25,
        displayString = R.string.puzzle_rank_nice
    ),
    Great(
        percentCutoff = 40,
        displayString = R.string.puzzle_rank_great
    ),
    Amazing(
        percentCutoff = 50,
        displayString = R.string.puzzle_rank_amazing
    ),
    Genius(
        percentCutoff = 70,
        displayString = R.string.puzzle_rank_genius
    );

    companion object {
        val sortedValues: List<PuzzleRanking> = values().sortedBy(PuzzleRanking::percentCutoff)
    }
}