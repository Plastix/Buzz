package io.github.plastix.buzz.changelog.versions

import androidx.compose.ui.text.AnnotatedString
import io.github.plastix.buzz.changelog.Change
import io.github.plastix.buzz.changelog.ChangeSet

val VERSION_1_alpha06 = ChangeSet(
    versionName = "1.0-alpha03",
    versionCode = 6,
    message = AnnotatedString(
        "Hiya! This is our initial public release"
    ),
    changes = listOf(
        Change.Improvement("Better support for larger font and display sizes"),
        Change.New("Generating puzzles now uses a floating action button"),
        Change.Fixed("Puzzle ordering now factors puzzle generating time as well as date"),
    )
)