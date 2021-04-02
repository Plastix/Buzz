package io.github.plastix.buzz.util

import java.text.ParseException
import java.text.SimpleDateFormat

fun formatDate(date: String): String = reformatDate(date, "yyyy-MM-dd", "EEEE MMMM d, yyyy")

private fun reformatDate(date: String, fromFmt: String, toFmt: String): String {
    val fromFormat = SimpleDateFormat(fromFmt)
    val toFormat = SimpleDateFormat(toFmt)
    return try {
        toFormat.format(fromFormat.parse(date)!!)
    } catch (e: ParseException) {
        date
    }
}