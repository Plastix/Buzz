package io.github.plastix.buzz.serialization

import com.squareup.moshi.Moshi

object Json {
    val instance by lazy { Moshi.Builder().build() }
}