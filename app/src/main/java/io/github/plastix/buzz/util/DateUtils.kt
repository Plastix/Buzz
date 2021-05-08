package io.github.plastix.buzz.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy")

fun LocalDateTime.toDisplayString(): String = format(DATE_FORMATTER)

fun String.parseDate(): LocalDateTime {
    return try {
        LocalDateTime.parse(this)
    } catch (e: DateTimeParseException) {
        try {
            LocalDate.parse(this).atTime(0, 0)
        } catch (e: DateTimeParseException) {
            LocalDateTime.now()
        }
    }
}
