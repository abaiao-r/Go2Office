package com.example.go2office.util
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
class WorkHoursCalculatorTest {

    @Test
    fun `GIVEN entry at 9am and exit at 5pm WHEN calculating session hours THEN should return 8 hours`() {
        val entry = LocalDateTime.of(2026, 2, 16, 9, 0)
        val exit = LocalDateTime.of(2026, 2, 16, 17, 0)
        val hours = WorkHoursCalculator.calculateSessionHours(entry, exit)
        assertEquals(8f, hours, 0.01f)
    }

    @Test
    fun `GIVEN entry before work hours WHEN calculating THEN should count from 7am`() {
        val entry = LocalDateTime.of(2026, 2, 16, 6, 0)
        val exit = LocalDateTime.of(2026, 2, 16, 12, 0)
        assertEquals(5f, WorkHoursCalculator.calculateSessionHours(entry, exit), 0.01f)
    }

    @Test
    fun `GIVEN exit after work hours WHEN calculating THEN should count until 7pm`() {
        val entry = LocalDateTime.of(2026, 2, 16, 14, 0)
        val exit = LocalDateTime.of(2026, 2, 16, 20, 0)
        assertEquals(5f, WorkHoursCalculator.calculateSessionHours(entry, exit), 0.01f)
    }

    @Test
    fun `GIVEN both times before work hours WHEN calculating THEN should return 0`() {
        val entry = LocalDateTime.of(2026, 2, 16, 5, 0)
        val exit = LocalDateTime.of(2026, 2, 16, 6, 30)
        assertEquals(0f, WorkHoursCalculator.calculateSessionHours(entry, exit), 0.01f)
    }

    @Test
    fun `GIVEN both times after work hours WHEN calculating THEN should return 0`() {
        val entry = LocalDateTime.of(2026, 2, 16, 20, 0)
        val exit = LocalDateTime.of(2026, 2, 16, 21, 0)
        assertEquals(0f, WorkHoursCalculator.calculateSessionHours(entry, exit), 0.01f)
    }

    @Test
    fun `GIVEN exit before entry WHEN calculating THEN should return 0`() {
        val entry = LocalDateTime.of(2026, 2, 16, 14, 0)
        val exit = LocalDateTime.of(2026, 2, 16, 10, 0)
        assertEquals(0f, WorkHoursCalculator.calculateSessionHours(entry, exit), 0.01f)
    }

    @Test
    fun `GIVEN sessions over 10h WHEN calculating daily hours THEN should cap at 10`() {
        val sessions = listOf(
            Pair(LocalDateTime.of(2026, 2, 16, 7, 0), LocalDateTime.of(2026, 2, 16, 19, 0))
        )
        assertEquals(10f, WorkHoursCalculator.calculateDailyHours(sessions), 0.01f)
    }

    @Test
    fun `GIVEN empty sessions WHEN calculating daily hours THEN should return 0`() {
        assertEquals(0f, WorkHoursCalculator.calculateDailyHours(emptyList()), 0.01f)
    }

    @Test
    fun `GIVEN time at 9am WHEN checking isWithinWorkHours THEN should return true`() {
        assertTrue(WorkHoursCalculator.isWithinWorkHours(LocalDateTime.of(2026, 2, 16, 9, 0)))
    }

    @Test
    fun `GIVEN time at 6am WHEN checking isWithinWorkHours THEN should return false`() {
        assertFalse(WorkHoursCalculator.isWithinWorkHours(LocalDateTime.of(2026, 2, 16, 6, 0)))
    }

    @Test
    fun `GIVEN 12 hours WHEN checking wouldBeCapped THEN should return true`() {
        assertTrue(WorkHoursCalculator.wouldBeCapped(12f))
    }

    @Test
    fun `GIVEN 8 hours WHEN checking wouldBeCapped THEN should return false`() {
        assertFalse(WorkHoursCalculator.wouldBeCapped(8f))
    }

    @Test
    fun `GIVEN multiple sessions WHEN calculating daily time THEN should use first entry and last exit`() {
        val entries = listOf(
            LocalDateTime.of(2026, 2, 16, 9, 0),
            LocalDateTime.of(2026, 2, 16, 13, 0)
        )
        val exits = listOf(
            LocalDateTime.of(2026, 2, 16, 12, 0),
            LocalDateTime.of(2026, 2, 16, 17, 0)
        )
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(LocalTime.of(9, 0), result.adjustedFirstEntry)
        assertEquals(LocalTime.of(17, 0), result.adjustedLastExit)
        assertEquals(8f, result.workedHours, 0.01f)
        assertTrue(result.countsAsDay)
        assertFalse(result.isCapped)
    }

    @Test
    fun `GIVEN entry before 7am and exit after 7am WHEN calculating THEN first entry should be 7am`() {
        val entries = listOf(LocalDateTime.of(2026, 2, 16, 6, 0))
        val exits = listOf(LocalDateTime.of(2026, 2, 16, 15, 0))
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(LocalTime.of(7, 0), result.adjustedFirstEntry)
        assertEquals(LocalTime.of(15, 0), result.adjustedLastExit)
        assertEquals(8f, result.workedHours, 0.01f)
    }

    @Test
    fun `GIVEN entry and exit both before 7am WHEN calculating THEN should count as day with 0 hours`() {
        val entries = listOf(LocalDateTime.of(2026, 2, 16, 5, 0))
        val exits = listOf(LocalDateTime.of(2026, 2, 16, 6, 30))
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertNull(result.adjustedFirstEntry)
        assertNull(result.adjustedLastExit)
        assertEquals(0f, result.workedHours, 0.01f)
        assertTrue(result.countsAsDay)
    }

    @Test
    fun `GIVEN exit after 7pm WHEN calculating THEN last exit should be 7pm`() {
        val entries = listOf(LocalDateTime.of(2026, 2, 16, 9, 0))
        val exits = listOf(LocalDateTime.of(2026, 2, 16, 21, 0))
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(LocalTime.of(9, 0), result.adjustedFirstEntry)
        assertEquals(LocalTime.of(19, 0), result.adjustedLastExit)
        assertEquals(10f, result.workedHours, 0.01f)
        assertFalse(result.isCapped)
    }

    @Test
    fun `GIVEN no exit WHEN calculating THEN last exit should default to 7pm`() {
        val entries = listOf(LocalDateTime.of(2026, 2, 16, 10, 0))
        val exits = listOf<LocalDateTime?>(null)
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(LocalTime.of(10, 0), result.adjustedFirstEntry)
        assertEquals(LocalTime.of(19, 0), result.adjustedLastExit)
        assertEquals(9f, result.workedHours, 0.01f)
    }

    @Test
    fun `GIVEN entry after 7pm WHEN calculating THEN entry should be 7pm with 0 hours`() {
        val entries = listOf(LocalDateTime.of(2026, 2, 16, 20, 0))
        val exits = listOf(LocalDateTime.of(2026, 2, 16, 21, 0))
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(LocalTime.of(19, 0), result.adjustedFirstEntry)
        assertEquals(LocalTime.of(19, 0), result.adjustedLastExit)
        assertEquals(0f, result.workedHours, 0.01f)
        assertTrue(result.countsAsDay)
    }

    @Test
    fun `GIVEN full day 6am to 9pm WHEN calculating THEN should cap at 10 hours`() {
        val entries = listOf(LocalDateTime.of(2026, 2, 16, 6, 0))
        val exits = listOf(LocalDateTime.of(2026, 2, 16, 21, 0))
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(LocalTime.of(7, 0), result.adjustedFirstEntry)
        assertEquals(LocalTime.of(19, 0), result.adjustedLastExit)
        assertEquals(10f, result.workedHours, 0.01f)
        assertTrue(result.isCapped)
    }

    @Test
    fun `GIVEN multiple sessions with breaks WHEN calculating THEN should use earliest entry and latest exit`() {
        val entries = listOf(
            LocalDateTime.of(2026, 2, 16, 6, 0),
            LocalDateTime.of(2026, 2, 16, 9, 0),
            LocalDateTime.of(2026, 2, 16, 14, 0)
        )
        val exits = listOf(
            LocalDateTime.of(2026, 2, 16, 6, 30),
            LocalDateTime.of(2026, 2, 16, 12, 0),
            LocalDateTime.of(2026, 2, 16, 18, 0)
        )
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(LocalTime.of(7, 0), result.adjustedFirstEntry)
        assertEquals(LocalTime.of(18, 0), result.adjustedLastExit)
        assertEquals(10f, result.workedHours, 0.01f)
        assertTrue(result.isCapped)
    }

    @Test
    fun `GIVEN empty entries WHEN calculating THEN should not count as day`() {
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(emptyList(), emptyList())
        assertNull(result.adjustedFirstEntry)
        assertNull(result.adjustedLastExit)
        assertEquals(0f, result.workedHours, 0.01f)
        assertFalse(result.countsAsDay)
    }

    @Test
    fun `GIVEN standard 9 to 5 WHEN calculating THEN should return 8 hours`() {
        val entries = listOf(LocalDateTime.of(2026, 2, 16, 9, 0))
        val exits = listOf(LocalDateTime.of(2026, 2, 16, 17, 0))
        val result = WorkHoursCalculator.calculateDailyTimeFromSessions(entries, exits)
        assertEquals(8f, result.workedHours, 0.01f)
        assertFalse(result.isCapped)
    }
}
