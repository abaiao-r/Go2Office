package com.example.go2office.util
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
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
}
