package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
import com.example.go2office.domain.model.MonthlyRequirements
import com.example.go2office.domain.model.OfficeSettings
import com.example.go2office.domain.repository.OfficeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class GetSuggestedOfficeDaysUseCaseTest {

    private lateinit var repository: OfficeRepository
    private lateinit var getMonthProgress: GetMonthProgressUseCase
    private lateinit var useCase: GetSuggestedOfficeDaysUseCase

    private val allWeekdays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    @Before
    fun setup() {
        repository = mockk()
        getMonthProgress = mockk()
        useCase = GetSuggestedOfficeDaysUseCase(repository, getMonthProgress)
    }

    @Test
    fun `GIVEN settings not configured WHEN getting suggestions THEN should return failure`() = runTest {
        coEvery { repository.getSettingsOnce() } returns null

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isFailure)
        assertEquals("Settings not configured", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN all requirements met WHEN getting suggestions THEN should return empty list`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 2,
            requiredHoursPerWeek = 16f,
            weekdayPreferences = allWeekdays
        )
        val progress = com.example.go2office.domain.model.MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            completedDays = 8,
            requiredHours = 64f,
            completedHours = 64f
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { getMonthProgress(any(), any()) } returns Result.success(progress)

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `GIVEN days needed WHEN getting suggestions THEN should return chronologically sorted list`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = allWeekdays
        )
        val progress = com.example.go2office.domain.model.MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 12,
            completedDays = 0,
            requiredHours = 96f,
            completedHours = 0f
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { getMonthProgress(any(), any()) } returns Result.success(progress)
        coEvery { repository.getOfficeDaysInRange(any(), any()) } returns emptyList()
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val suggestions = result.getOrNull()!!
        assertTrue(suggestions.isNotEmpty())

        // Verify chronological order
        for (i in 0 until suggestions.size - 1) {
            assertTrue(suggestions[i].date <= suggestions[i + 1].date)
        }
    }

    @Test
    fun `GIVEN already marked days WHEN getting suggestions THEN should exclude marked days`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 2,
            requiredHoursPerWeek = 16f,
            weekdayPreferences = allWeekdays
        )
        val progress = com.example.go2office.domain.model.MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 8,
            completedDays = 2,
            requiredHours = 64f,
            completedHours = 16f
        )
        val markedDays = listOf(
            DailyEntry(date = LocalDate.of(2026, 2, 16), wasInOffice = true, hoursWorked = 8f),
            DailyEntry(date = LocalDate.of(2026, 2, 17), wasInOffice = true, hoursWorked = 8f)
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { getMonthProgress(any(), any()) } returns Result.success(progress)
        coEvery { repository.getOfficeDaysInRange(any(), any()) } returns markedDays
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val suggestions = result.getOrNull()!!
        val suggestedDates = suggestions.map { it.date }
        assertFalse(suggestedDates.contains(LocalDate.of(2026, 2, 16)))
        assertFalse(suggestedDates.contains(LocalDate.of(2026, 2, 17)))
    }

    @Test
    fun `GIVEN holidays in month WHEN getting suggestions THEN should exclude holiday dates`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = allWeekdays
        )
        val progress = com.example.go2office.domain.model.MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 12,
            completedDays = 0,
            requiredHours = 96f,
            completedHours = 0f
        )
        val holidays = listOf(
            Holiday(date = LocalDate.of(2026, 2, 16), description = "Holiday", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 17), description = "Holiday 2", type = HolidayType.PUBLIC_HOLIDAY)
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { getMonthProgress(any(), any()) } returns Result.success(progress)
        coEvery { repository.getOfficeDaysInRange(any(), any()) } returns emptyList()
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns holidays

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val suggestions = result.getOrNull()!!
        val suggestedDates = suggestions.map { it.date }
        assertFalse(suggestedDates.contains(LocalDate.of(2026, 2, 16)))
        assertFalse(suggestedDates.contains(LocalDate.of(2026, 2, 17)))
    }

    @Test
    fun `GIVEN suggestions needed WHEN getting THEN should never suggest weekends`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = allWeekdays
        )
        val progress = com.example.go2office.domain.model.MonthProgress(
            yearMonth = YearMonth.of(2026, 2),
            requiredDays = 20,
            completedDays = 0,
            requiredHours = 160f,
            completedHours = 0f
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { getMonthProgress(any(), any()) } returns Result.success(progress)
        coEvery { repository.getOfficeDaysInRange(any(), any()) } returns emptyList()
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val suggestions = result.getOrNull()!!
        suggestions.forEach { suggestion ->
            val dayOfWeek = suggestion.date.dayOfWeek
            assertNotEquals(DayOfWeek.SATURDAY, dayOfWeek)
            assertNotEquals(DayOfWeek.SUNDAY, dayOfWeek)
        }
    }
}

