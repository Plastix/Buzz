package io.github.plastix.buzz.persistence

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun serializeCharSet(set: Set<Char>): String {
        return set.joinToString(separator = ",")
    }

    @TypeConverter
    fun deserializeCharSet(string: String): Set<Char> {
        return if (string.isBlank()) {
            emptySet()
        } else {
            string.split(",").map { it[0] }.toSet()
        }
    }

    @TypeConverter
    fun serializeStringSet(set: Set<String>): String {
        return set.joinToString(separator = ",")
    }

    @TypeConverter
    fun deserializeStringSet(string: String): Set<String> {
        return if (string.isBlank()) {
            emptySet()
        } else {
            string.split(",").toSet()
        }
    }

    @TypeConverter
    fun serializeCharList(list: List<Char>): String {
        return list.joinToString(separator = ",")
    }

    @TypeConverter
    fun deserializeCharList(string: String): List<Char> {
        return if (string.isBlank()) {
            emptyList()
        } else {
            string.split(",").map { it[0] }
        }
    }
}
