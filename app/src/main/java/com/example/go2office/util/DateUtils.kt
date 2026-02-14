package com.example.go2office.util
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
object DateUtils {
    fun isWeekend(date: LocalDate): Boolean {
        return date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
    }
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
    fun getCurrentMonth(zoneId: ZoneId = ZoneId.systemDefault()): YearMonth {
        return YearMonth.now(zoneId)
    }
    fun getCurrentDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return LocalDate.now(zoneId)
    }
    fun getDaysUntilEndOfMonth(date: LocalDate): Int {
        val endOfMonth = YearMonth.from(date).atEndOfMonth()
        return ChronoUnit.DAYS.between(date, endOfMonth).toInt()
    }
    fun getRemainingWeekdaysInMonth(date: LocalDate): Int {
        val endOfMonth = YearMonth.from(date).atEndOfMonth()
        return getWorkingDaysInRange(date, endOfMonth).size
    }
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
    fun getMonthBounds(yearMonth: YearMonth): Pair<LocalDate, LocalDate> {
        return Pair(yearMonth.atDay(1), yearMonth.atEndOfMonth())
    }
    fun isPast(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        return date.isBefore(getCurrentDate(zoneId))
    }
    fun isFuture(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        return date.isAfter(getCurrentDate(zoneId))
    }
    fun isToday(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        return date == getCurrentDate(zoneId)
    }
}
