package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.model.MonthlyRequirements
import com.example.go2office.domain.repository.OfficeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class GetMonthProgressUseCaseTest {

    private lateinit var repository: OfficeRepository
    private lateinit var calculateRequirements: CalculateMonthlyRequirementsUseCase
    private lateinit var useCase: GetMonthProgressUseCase

    @Before
    fun setup() {
        repository = mockk()
        calculateRequirements = mockk()
        useCase = GetMonthProgressUseCase(repository, calculateRequirements)
    }

    @Test
    fun `GIVEN requirements calculation fails WHEN getting progress THEN should return failure`() = runTest {
        coEvery { calculateRequirements(any(), any()) } returns Result.failure(Exception("Settings not configured"))

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN no entries exist WHEN getting progress THEN should return 0 completed`() = runTest {
        val requirements = MonthlyRequirements(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            requiredHours = 64f,
            totalWeekdaysInMonth = 20,
            holidaysCount = 0
        )
        coEvery { calculateRequirements(any(), any()) } returns Result.success(requirements)
        coEvery { repository.getDailyEntriesInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.completedDays)
        assertEquals(0f, result.getOrNull()?.completedHours)
    }

    @Test
    fun `GIVEN 3 office days completed WHEN getting progress THEN should return correct completed count`() = runTest {
        val requirements = MonthlyRequirements(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            requiredHours = 64f,
            totalWeekdaysInMonth = 20,
            holidaysCount = 0
        )
        val entries = listOf(
            DailyEntry(date = LocalDate.of(2026, 2, 2), wasInOffice = true, hoursWorked = 8f),
            DailyEntry(date = LocalDate.of(2026, 2, 3), wasInOffice = true, hoursWorked = 7.5f),
            DailyEntry(date = LocalDate.of(2026, 2, 4), wasInOffice = true, hoursWorked = 8.5f)
        )
        coEvery { calculateRequirements(any(), any()) } returns Result.success(requirements)
        coEvery { repository.getDailyEntriesInRangeOnce(any(), any()) } returns entries

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.completedDays)
        assertEquals(24f, result.getOrNull()?.completedHours)
    }

    @Test
    fun `GIVEN mixed office and non-office entries WHEN getting progress THEN should only count office days`() = runTest {
        val requirements = MonthlyRequirements(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            requiredHours = 64f,
            totalWeekdaysInMonth = 20,
            holidaysCount = 0
        )
        val entries = listOf(
            DailyEntry(date = LocalDate.of(2026, 2, 2), wasInOffice = true, hoursWorked = 8f),
            DailyEntry(date = LocalDate.of(2026, 2, 3), wasInOffice = false, hoursWorked = 0f),
            DailyEntry(date = LocalDate.of(2026, 2, 4), wasInOffice = true, hoursWorked = 8f),
            DailyEntry(date = LocalDate.of(2026, 2, 5), wasInOffice = false, hoursWorked = 0f)
        )
        coEvery { calculateRequirements(any(), any()) } returns Result.success(requirements)
        coEvery { repository.getDailyEntriesInRangeOnce(any(), any()) } returns entries

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.completedDays)
        assertEquals(16f, result.getOrNull()?.completedHours)
    }

    @Test
    fun `GIVEN all days completed WHEN getting progress THEN remaining should be 0`() = runTest {
        val requirements = MonthlyRequirements(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 2,
            requiredHours = 16f,
            totalWeekdaysInMonth = 20,
            holidaysCount = 0
        )
        val entries = listOf(
            DailyEntry(date = LocalDate.of(2026, 2, 2), wasInOffice = true, hoursWorked = 8f),
            DailyEntry(date = LocalDate.of(2026, 2, 4), wasInOffice = true, hoursWorked = 8f)
        )
        coEvery { calculateRequirements(any(), any()) } returns Result.success(requirements)
        coEvery { repository.getDailyEntriesInRangeOnce(any(), any()) } returns entries

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val progress = result.getOrNull()!!
        assertEquals(0, progress.remainingDays)
        assertEquals(0f, progress.remainingHours, 0.01f)
    }

    @Test
    fun `GIVEN partial completion WHEN getting progress THEN should calculate remaining correctly`() = runTest {
        val requirements = MonthlyRequirements(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            requiredHours = 64f,
            totalWeekdaysInMonth = 20,
            holidaysCount = 0
        )
        val entries = listOf(
            DailyEntry(date = LocalDate.of(2026, 2, 2), wasInOffice = true, hoursWorked = 8f),
            DailyEntry(date = LocalDate.of(2026, 2, 3), wasInOffice = true, hoursWorked = 8f),
            DailyEntry(date = LocalDate.of(2026, 2, 4), wasInOffice = true, hoursWorked = 8f)
        )
        coEvery { calculateRequirements(any(), any()) } returns Result.success(requirements)
        coEvery { repository.getDailyEntriesInRangeOnce(any(), any()) } returns entries

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val progress = result.getOrNull()!!
        assertEquals(5, progress.remainingDays)
        assertEquals(40f, progress.remainingHours, 0.01f)
    }

    @Test
    fun `GIVEN over requirement completion WHEN getting progress THEN remaining should be 0 not negative`() = runTest {
        val requirements = MonthlyRequirements(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 2,
            requiredHours = 16f,
            totalWeekdaysInMonth = 20,
            holidaysCount = 0
        )
        val entries = listOf(
            DailyEntry(date = LocalDate.of(2026, 2, 2), wasInOffice = true, hoursWorked = 10f),
            DailyEntry(date = LocalDate.of(2026, 2, 3), wasInOffice = true, hoursWorked = 10f),
            DailyEntry(date = LocalDate.of(2026, 2, 4), wasInOffice = true, hoursWorked = 10f)
        )
        coEvery { calculateRequirements(any(), any()) } returns Result.success(requirements)
        coEvery { repository.getDailyEntriesInRangeOnce(any(), any()) } returns entries

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val progress = result.getOrNull()!!
        assertEquals(0, progress.remainingDays.coerceAtLeast(0))
        assertEquals(0f, progress.remainingHours.coerceAtLeast(0f), 0.01f)
    }
}
