package io.github.plastix.buzz.dataset

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.github.plastix.buzz.core.Constants
import io.github.plastix.buzz.core.size
import io.github.plastix.buzz.core.toCharacterSet
import io.plastix.github.buzz.dataset.Database
import java.io.File
import java.nio.file.Paths


const val DB_EXPORT_FOLDER = "app/src/main/assets/database/"
const val DB_EXPORT_NAME = "dictionary.db"

/**
 * Preprocesses a Dictionary of words into a SQLite database which is bundled with the main
 * Android app.
 */
fun main() {
    // Delete the existing table file
    File("$DB_EXPORT_FOLDER$DB_EXPORT_NAME").delete()

    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$DB_EXPORT_FOLDER$DB_EXPORT_NAME")
    Database.Schema.create(driver)
    val database = Database(driver)
    val queries = database.schemaQueries

    val wordPath =
        Paths.get("dataset/src/main/java/io/github/plastix/buzz/dataset/words").toAbsolutePath()
            .toString()
    val reader = File(wordPath).bufferedReader()
    reader.lines().forEach { word ->
        if (word.isValid()) {
            val charSet = word.toCharacterSet()
            queries.addWord(charSet.toLong(), word)

            if (charSet.size == Constants.LETTER_COUNT) {
                queries.addPuzzleSeed(charSet.toLong())
            }
        }
    }

}

/**
 * Words are only valid answers if are at least 4 letters and contain no special characters.
 * Spaces or capital checking also removes proper nouns from our dictionary list
 */
fun String.isValid(): Boolean {
    return length >= 4 && none {
        it < 'a' || it > 'z'
    }
}
