package io.github.plastix.buzz.changelog

import androidx.compose.ui.text.AnnotatedString

sealed class Change(open val message: String) {
    data class New(override val message: String) : Change(message)
    data class Fixed(override val message: String) : Change(message)
    data class Improvement(override val message: String) : Change(message)
}

data class ChangeSet(
    val versionName: String,
    val versionCode: Int,
    val message: AnnotatedString,
    val changes: List<Change>
)