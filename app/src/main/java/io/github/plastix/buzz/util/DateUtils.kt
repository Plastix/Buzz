package io.github.plastix.buzz.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy")

fun LocalDate.toDisplayString(): String = format(DATE_FORMATTER)

fun String.parseDate(): LocalDate {
    return try {
        LocalDate.parse(this)
    } catch (e: DateTimeParseException) {
        LocalDate.now()
    }
}