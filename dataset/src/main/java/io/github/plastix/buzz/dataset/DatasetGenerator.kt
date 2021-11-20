package io.github.plastix.buzz.dataset

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.github.plastix.buzz.core.Constants
import io.github.plastix.buzz.core.size
import io.github.plastix.buzz.core.toCharacterSet
import io.plastix.github.buzz.dataset.Database
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis


const val DB_EXPORT_FOLDER = "app/src/main/assets/database/"
const val DB_EXPORT_NAME = "dictionary.db"
const val RESOURCES_PATH = "dataset/src/main/resources/"

/**
 * Preprocesses a Dictionary of words into a SQLite database which is bundled with the main
 * Android app.
 */
fun main() {
    var totalWords = 0
    var processedCount = 0
    val profaneWords = mutableSetOf<String>()
    val puzzleSeeds = mutableSetOf<Long>()
    val durationMs = measureTimeMillis {
        // Delete the existing table file
        File("$DB_EXPORT_FOLDER$DB_EXPORT_NAME").delete()

        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$DB_EXPORT_FOLDER$DB_EXPORT_NAME")
        Database.Schema.create(driver)
        val database = Database(driver)
        val queries = database.schemaQueries

        val badWords = Paths.get("${RESOURCES_PATH}bad_words").toAbsolutePath().toString()
        val denyList: Set<String> = File(badWords).readLines().toSet()

        val dictionaryPath = Paths.get("${RESOURCES_PATH}words").toAbsolutePath().toString()
        val reader = File(dictionaryPath).bufferedReader()
        queries.transaction {
            reader.lines().forEach { word ->
                val trimmedWord = word.trim()
                if (trimmedWord.isValid()) {
                    if (trimmedWord !in denyList) {
                        val charSet = trimmedWord.toCharacterSet()
                        queries.addWord(charSet.toLong(), trimmedWord)

                        if (charSet.size == Constants.LETTER_COUNT) {
                            val seed = charSet.toLong()
                            queries.addPuzzleSeed(seed)
                            puzzleSeeds.add(seed)
                        }
                        processedCount++

                    } else {
                        profaneWords.add(trimmedWord)
                    }
                }
                totalWords++
            }
        }
    }

    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs)
    println(
        """
        Processed $totalWords words from word list in $seconds seconds!
        Final count: $processedCount
        Blocked profane words: ${profaneWords.size}

        Found ${puzzleSeeds.size} possible puzzle seeds
    """.trimIndent()
    )
}

/**
 * Words are only valid answers if are at least 4 letters and contain no special characters.
 * Spaces or capital checking also removes proper nouns from our dictionary list
 */
private fun String.isValid(): Boolean {
    return length >= 4 && none {
        it < 'a' || it > 'z'
    }
}
