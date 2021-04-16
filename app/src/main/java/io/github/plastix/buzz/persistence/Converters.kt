package io.github.plastix.buzz.persistence

import androidx.room.TypeConverter
import io.github.plastix.buzz.PuzzleType
import io.github.plastix.buzz.util.parseDate
import java.time.LocalDate

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

    @TypeConverter
    fun serializeDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun deserializeDate(string: String): LocalDate {
        return string.parseDate()
    }

    @TypeConverter
    fun serializePuzzleType(puzzleType: PuzzleType): String {
        return puzzleType.stringKey
    }

    @TypeConverter
    fun deserializePuzzleType(string: String): PuzzleType {
        return PuzzleType.fromStringKey(string)
    }
}
