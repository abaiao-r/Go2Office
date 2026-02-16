package com.example.go2office.util

import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class DateUtilsTest {

    // isWeekend tests
    @Test
    fun `GIVEN a Saturday date WHEN checking isWeekend THEN should return true`() {
        val saturday = LocalDate.of(2026, 2, 14) // Saturday
        assertTrue(DateUtils.isWeekend(saturday))
    }

    @Test
    fun `GIVEN a Sunday date WHEN checking isWeekend THEN should return true`() {
        val sunday = LocalDate.of(2026, 2, 15) // Sunday
        assertTrue(DateUtils.isWeekend(sunday))
    }

    @Test
    fun `GIVEN a Monday date WHEN checking isWeekend THEN should return false`() {
        val monday = LocalDate.of(2026, 2, 16) // Monday
        assertFalse(DateUtils.isWeekend(monday))
    }

    @Test
    fun `GIVEN a Wednesday date WHEN checking isWeekend THEN should return false`() {
        val wednesday = LocalDate.of(2026, 2, 18) // Wednesday
        assertFalse(DateUtils.isWeekend(wednesday))
    }

    @Test
    fun `GIVEN a Friday date WHEN checking isWeekend THEN should return false`() {
        val friday = LocalDate.of(2026, 2, 20) // Friday
        assertFalse(DateUtils.isWeekend(friday))
    }

    // getWeekdaysInMonth tests
    @Test
    fun `GIVEN February 2026 WHEN getting weekdays in month THEN should return 20`() {
        val february2026 = YearMonth.of(2026, 2)
        val weekdays = DateUtils.getWeekdaysInMonth(february2026)
        assertEquals(20, weekdays)
    }

    @Test
    fun `GIVEN March 2026 WHEN getting weekdays in month THEN should return 22`() {
        val march2026 = YearMonth.of(2026, 3)
        val weekdays = DateUtils.getWeekdaysInMonth(march2026)
        assertEquals(22, weekdays)
    }

    @Test
    fun `GIVEN February 2026 with 2 holidays excluded WHEN getting weekdays THEN should return 18`() {
        val february2026 = YearMonth.of(2026, 2)
        val holidays = listOf(
            LocalDate.of(2026, 2, 16), // Monday
            LocalDate.of(2026, 2, 17)  // Tuesday
        )
        val weekdays = DateUtils.getWeekdaysInMonth(february2026, holidays)
        assertEquals(18, weekdays)
    }

    @Test
    fun `GIVEN month with weekend holidays excluded WHEN getting weekdays THEN weekends should not affect count`() {
        val february2026 = YearMonth.of(2026, 2)
        val holidays = listOf(
            LocalDate.of(2026, 2, 14), // Saturday - should not affect
            LocalDate.of(2026, 2, 15)  // Sunday - should not affect
        )
        val weekdays = DateUtils.getWeekdaysInMonth(february2026, holidays)
        assertEquals(20, weekdays) // Same as without holidays
    }

    // getWorkingDaysInRange tests
    @Test
    fun `GIVEN a week range WHEN getting working days THEN should return 5 weekdays`() {
        val startDate = LocalDate.of(2026, 2, 16) // Monday
        val endDate = LocalDate.of(2026, 2, 22)   // Sunday
        val workingDays = DateUtils.getWorkingDaysInRange(startDate, endDate)
        assertEquals(5, workingDays.size)
    }

    @Test
    fun `GIVEN weekend only range WHEN getting working days THEN should return empty list`() {
        val startDate = LocalDate.of(2026, 2, 14) // Saturday
        val endDate = LocalDate.of(2026, 2, 15)   // Sunday
        val workingDays = DateUtils.getWorkingDaysInRange(startDate, endDate)
        assertTrue(workingDays.isEmpty())
    }

    @Test
    fun `GIVEN single weekday WHEN getting working days THEN should return list with one day`() {
        val monday = LocalDate.of(2026, 2, 16)
        val workingDays = DateUtils.getWorkingDaysInRange(monday, monday)
        assertEquals(1, workingDays.size)
        assertEquals(monday, workingDays[0])
    }

    @Test
    fun `GIVEN two weeks range WHEN getting working days THEN should return 10 weekdays`() {
        val startDate = LocalDate.of(2026, 2, 16) // Monday
        val endDate = LocalDate.of(2026, 2, 27)   // Friday
        val workingDays = DateUtils.getWorkingDaysInRange(startDate, endDate)
        assertEquals(10, workingDays.size)
    }

    // getMonthBounds tests
    @Test
    fun `GIVEN February 2026 WHEN getting month bounds THEN should return correct start and end dates`() {
        val february2026 = YearMonth.of(2026, 2)
        val (start, end) = DateUtils.getMonthBounds(february2026)
        assertEquals(LocalDate.of(2026, 2, 1), start)
        assertEquals(LocalDate.of(2026, 2, 28), end)
    }

    @Test
    fun `GIVEN leap year February WHEN getting month bounds THEN end should be 29th`() {
        val february2024 = YearMonth.of(2024, 2) // Leap year
        val (start, end) = DateUtils.getMonthBounds(february2024)
        assertEquals(LocalDate.of(2024, 2, 1), start)
        assertEquals(LocalDate.of(2024, 2, 29), end)
    }

    @Test
    fun `GIVEN March 2026 WHEN getting month bounds THEN end should be 31st`() {
        val march2026 = YearMonth.of(2026, 3)
        val (start, end) = DateUtils.getMonthBounds(march2026)
        assertEquals(LocalDate.of(2026, 3, 1), start)
        assertEquals(LocalDate.of(2026, 3, 31), end)
    }

    // getDaysUntilEndOfMonth tests
    @Test
    fun `GIVEN first day of February WHEN getting days until end THEN should return 27`() {
        val firstDay = LocalDate.of(2026, 2, 1)
        val daysUntilEnd = DateUtils.getDaysUntilEndOfMonth(firstDay)
        assertEquals(27, daysUntilEnd)
    }

    @Test
    fun `GIVEN last day of month WHEN getting days until end THEN should return 0`() {
        val lastDay = LocalDate.of(2026, 2, 28)
        val daysUntilEnd = DateUtils.getDaysUntilEndOfMonth(lastDay)
        assertEquals(0, daysUntilEnd)
    }

    @Test
    fun `GIVEN mid month date WHEN getting days until end THEN should return correct count`() {
        val midMonth = LocalDate.of(2026, 2, 15)
        val daysUntilEnd = DateUtils.getDaysUntilEndOfMonth(midMonth)
        assertEquals(13, daysUntilEnd)
    }

    // getRemainingWeekdaysInMonth tests
    @Test
    fun `GIVEN first day of February 2026 WHEN getting remaining weekdays THEN should return 20`() {
        val firstDay = LocalDate.of(2026, 2, 2) // Monday (first weekday)
        val remainingWeekdays = DateUtils.getRemainingWeekdaysInMonth(firstDay)
        assertEquals(20, remainingWeekdays)
    }

    @Test
    fun `GIVEN last weekday of month WHEN getting remaining weekdays THEN should return 1`() {
        val lastFriday = LocalDate.of(2026, 2, 27) // Friday
        val remainingWeekdays = DateUtils.getRemainingWeekdaysInMonth(lastFriday)
        assertEquals(1, remainingWeekdays)
    }

    // isPast tests
    @Test
    fun `GIVEN yesterday date WHEN checking isPast THEN should return true`() {
        val yesterday = LocalDate.now().minusDays(1)
        assertTrue(DateUtils.isPast(yesterday))
    }

    @Test
    fun `GIVEN today date WHEN checking isPast THEN should return false`() {
        val today = LocalDate.now()
        assertFalse(DateUtils.isPast(today))
    }

    @Test
    fun `GIVEN tomorrow date WHEN checking isPast THEN should return false`() {
        val tomorrow = LocalDate.now().plusDays(1)
        assertFalse(DateUtils.isPast(tomorrow))
    }

    // getAllDatesInMonth tests
    @Test
    fun `GIVEN February 2026 WHEN getting all dates THEN should return 28 dates`() {
        val february2026 = YearMonth.of(2026, 2)
        val allDates = DateUtils.getAllDatesInMonth(february2026)
        assertEquals(28, allDates.size)
    }

    @Test
    fun `GIVEN March 2026 WHEN getting all dates THEN should return 31 dates`() {
        val march2026 = YearMonth.of(2026, 3)
        val allDates = DateUtils.getAllDatesInMonth(march2026)
        assertEquals(31, allDates.size)
    }

    @Test
    fun `GIVEN month WHEN getting all dates THEN first date should be day 1`() {
        val february2026 = YearMonth.of(2026, 2)
        val allDates = DateUtils.getAllDatesInMonth(february2026)
        assertEquals(1, allDates.first().dayOfMonth)
    }

    @Test
    fun `GIVEN February 2026 WHEN getting all dates THEN last date should be day 28`() {
        val february2026 = YearMonth.of(2026, 2)
        val allDates = DateUtils.getAllDatesInMonth(february2026)
        assertEquals(28, allDates.last().dayOfMonth)
    }
}

