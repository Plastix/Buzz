package io.github.plastix.buzz

enum class PuzzleRanking(val percentCutoff: Int) {
    Beginner(0),
    GoodStart(2),
    MovingUp(5),
    Good(8),
    Solid(15),
    Nice(25),
    Great(40),
    Amazing(50),
    Genius(70);
}