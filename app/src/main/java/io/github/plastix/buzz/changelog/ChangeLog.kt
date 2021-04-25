package io.github.plastix.buzz.changelog

import io.github.plastix.buzz.changelog.versions.VERSION_1_alpha06

val CHANGELOG = listOf(
    VERSION_1_alpha06
).sortedByDescending(ChangeSet::versionCode)