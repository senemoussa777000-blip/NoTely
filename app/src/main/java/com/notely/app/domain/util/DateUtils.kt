package com.notely.app.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val fr = Locale.FRENCH

/** Petits utilitaires de dates, en français, fuseau local. */
object DateUtils {

    fun formatDate(millis: Long): String =
        SimpleDateFormat("EEEE d MMMM yyyy", fr).format(Date(millis)).replaceFirstChar { it.uppercaseChar() }

    fun formatShortDate(millis: Long): String =
        SimpleDateFormat("d MMM yyyy", fr).format(Date(millis))

    fun formatTime(millis: Long): String =
        SimpleDateFormat("HH:mm", fr).format(Date(millis))

    fun formatDateTime(millis: Long): String =
        "${formatShortDate(millis)} · ${formatTime(millis)}"

    fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun endOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun isToday(millis: Long): Boolean {
        val now = System.currentTimeMillis()
        return startOfDay(millis) == startOfDay(now)
    }

    fun isOverdue(millis: Long): Boolean = millis < startOfDay(System.currentTimeMillis())

    fun monthTitle(millis: Long): String =
        SimpleDateFormat("MMMM yyyy", fr).format(Date(millis)).replaceFirstChar { it.uppercaseChar() }

    /** Nombre de jours du mois contenant [millis]. */
    fun daysInMonth(millis: Long): Int =
        Calendar.getInstance().apply { timeInMillis = millis }.getActualMaximum(Calendar.DAY_OF_MONTH)

    /**
     * Décalage du 1er jour du mois dans une grille qui commence le lundi.
     * 0 = le mois commence un lundi.
     */
    fun firstDayOffset(millis: Long): Int {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = dimanche
        return (dayOfWeek + 5) % 7
    }

    fun millisForDay(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun addMonths(millis: Long, months: Int): Long =
        Calendar.getInstance().apply {
            timeInMillis = millis
            add(Calendar.MONTH, months)
        }.timeInMillis
}
