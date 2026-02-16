package com.example.go2office.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.time.YearMonth

class MonthProgressTest {

    @Test
    fun `GIVEN required 10 days and completed 5 WHEN getting remainingDays THEN should return 5`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 10,
            completedDays = 5,
            requiredHours = 80f,
            completedHours = 40f
        )

        assertEquals(5, progress.remainingDays)
    }

    @Test
    fun `GIVEN completed more than required WHEN getting remainingDays THEN should return 0`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 5,
            completedDays = 8,
            requiredHours = 40f,
            completedHours = 64f
        )

        assertEquals(0, progress.remainingDays)
    }

    @Test
    fun `GIVEN required 80h and completed 30h WHEN getting remainingHours THEN should return 50h`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 10,
            completedDays = 4,
            requiredHours = 80f,
            completedHours = 30f
        )

        assertEquals(50f, progress.remainingHours, 0.01f)
    }

    @Test
    fun `GIVEN completed more hours than required WHEN getting remainingHours THEN should return 0`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 5,
            completedDays = 7,
            requiredHours = 40f,
            completedHours = 60f
        )

        assertEquals(0f, progress.remainingHours, 0.01f)
    }

    @Test
    fun `GIVEN half completion WHEN getting daysPercentComplete THEN should return 50`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 10,
            completedDays = 5,
            requiredHours = 80f,
            completedHours = 40f
        )

        assertEquals(50f, progress.daysPercentComplete, 0.01f)
    }

    @Test
    fun `GIVEN over 100 percent completion WHEN getting daysPercentComplete THEN should cap at 100`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 5,
            completedDays = 10,
            requiredHours = 40f,
            completedHours = 80f
        )

        assertEquals(100f, progress.daysPercentComplete, 0.01f)
    }

    @Test
    fun `GIVEN 0 required days WHEN getting daysPercentComplete THEN should return 0`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 0,
            completedDays = 5,
            requiredHours = 0f,
            completedHours = 40f
        )

        assertEquals(0f, progress.daysPercentComplete, 0.01f)
    }

    @Test
    fun `GIVEN half hour completion WHEN getting hoursPercentComplete THEN should return 50`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 10,
            completedDays = 5,
            requiredHours = 80f,
            completedHours = 40f
        )

        assertEquals(50f, progress.hoursPercentComplete, 0.01f)
    }

    @Test
    fun `GIVEN days and hours complete WHEN checking isComplete THEN should return true`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            completedDays = 8,
            requiredHours = 64f,
            completedHours = 64f
        )

        assertTrue(progress.isComplete)
    }

    @Test
    fun `GIVEN days complete but hours not WHEN checking isComplete THEN should return false`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            completedDays = 8,
            requiredHours = 64f,
            completedHours = 50f
        )

        assertFalse(progress.isComplete)
    }

    @Test
    fun `GIVEN hours complete but days not WHEN checking isComplete THEN should return false`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            completedDays = 6,
            requiredHours = 64f,
            completedHours = 70f
        )

        assertFalse(progress.isComplete)
    }

    @Test
    fun `GIVEN over completion WHEN checking isComplete THEN should return true`() {
        val progress = MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            completedDays = 10,
            requiredHours = 64f,
            completedHours = 80f
        )

        assertTrue(progress.isComplete)
    }
}

