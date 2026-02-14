package com.example.go2office.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Utility functions for date and calendar calculations.
 */
object DateUtils {

    /**
     * Check if a date falls on a weekend (Saturday or Sunday).
     */
    fun isWeekend(date: LocalDate): Boolean {
        return date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
    }

    /**
     * Count the number of weekdays (Monday-Friday) in a given month.
     */
    fun getWeekdaysInMonth(yearMonth: YearMonth, excludeDates: List<LocalDate> = emptyList()): Int {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        var count = 0
        var currentDate = firstDay

        while (!currentDate.isAfter(lastDay)) {
            if (!isWeekend(currentDate) && currentDate !in excludeDates) {
                count++
            }
            currentDate = currentDate.plusDays(1)
        }

        return count
    }

    /**
     * Get all working days (weekdays) in a date range.
     */
    fun getWorkingDaysInRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val workingDays = mutableListOf<LocalDate>()
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            if (!isWeekend(currentDate)) {
                workingDays.add(currentDate)
            }
            currentDate = currentDate.plusDays(1)
        }

        return workingDays
    }

    /**
     * Get the current month in the specified timezone.
     */
    fun getCurrentMonth(zoneId: ZoneId = ZoneId.systemDefault()): YearMonth {
        return YearMonth.now(zoneId)
    }

    /**
     * Get the current date in the specified timezone.
     */
    fun getCurrentDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return LocalDate.now(zoneId)
    }

    /**
     * Get the number of days until the end of the month from a given date.
     */
    fun getDaysUntilEndOfMonth(date: LocalDate): Int {
        val endOfMonth = YearMonth.from(date).atEndOfMonth()
        return ChronoUnit.DAYS.between(date, endOfMonth).toInt()
    }

    /**
     * Get the number of weekdays remaining in the month from a given date.
     */
    fun getRemainingWeekdaysInMonth(date: LocalDate): Int {
        val endOfMonth = YearMonth.from(date).atEndOfMonth()
        return getWorkingDaysInRange(date, endOfMonth).size
    }

    /**
     * Get all dates in a month.
     */
    fun getAllDatesInMonth(yearMonth: YearMonth): List<LocalDate> {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        val dates = mutableListOf<LocalDate>()
        var currentDate = firstDay

        while (!currentDate.isAfter(lastDay)) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        return dates
    }

    /**
     * Get the first and last date of a month.
     */
    fun getMonthBounds(yearMonth: YearMonth): Pair<LocalDate, LocalDate> {
        return Pair(yearMonth.atDay(1), yearMonth.atEndOfMonth())
    }

    /**
     * Check if a date is in the past (before today).
     */
    fun isPast(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        return date.isBefore(getCurrentDate(zoneId))
    }

    /**
     * Check if a date is in the future (after today).
     */
    fun isFuture(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        return date.isAfter(getCurrentDate(zoneId))
    }

    /**
     * Check if a date is today.
     */
    fun isToday(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        return date == getCurrentDate(zoneId)
    }
}

