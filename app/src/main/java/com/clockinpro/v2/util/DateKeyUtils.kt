package com.clockinpro.v2.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateKeyUtils {
    private val dateKeyFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): LocalDate = LocalDate.now()

    fun toDateKey(date: LocalDate): String = date.format(dateKeyFormatter)

    fun parseDateKey(dateKey: String): LocalDate = LocalDate.parse(dateKey, dateKeyFormatter)

    fun formatDate(date: LocalDate, locale: Locale = Locale.getDefault()): String =
        date.format(DateTimeFormatter.ofPattern("MMM d, EEE", locale))

    fun formatMonth(month: YearMonth, locale: Locale = Locale.getDefault()): String =
        month.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale))

    fun formatTime(hour: Int, minute: Int): String = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}
