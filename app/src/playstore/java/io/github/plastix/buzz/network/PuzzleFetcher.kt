package io.github.plastix.buzz.network

import io.github.plastix.buzz.Puzzle
import io.github.plastix.buzz.Result
import javax.inject.Inject

class PuzzleFetcher @Inject constructor() {

    fun fetchLatestPuzzles(): Result<List<Puzzle>> {
        // No-op: Play Store release does not support downloading official NYT puzzles
        return Result.Success(emptyList())
    }
}
