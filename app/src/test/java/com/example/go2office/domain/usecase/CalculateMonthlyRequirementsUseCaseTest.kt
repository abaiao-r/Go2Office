package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
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

class CalculateMonthlyRequirementsUseCaseTest {

    private lateinit var repository: OfficeRepository
    private lateinit var useCase: CalculateMonthlyRequirementsUseCase

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
        useCase = CalculateMonthlyRequirementsUseCase(repository)
    }

    @Test
    fun `GIVEN settings not configured WHEN calculating requirements THEN should return failure`() = runTest {
        coEvery { repository.getSettingsOnce() } returns null

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isFailure)
        assertEquals("Settings not configured", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN 5 days per week required WHEN calculating for Feb 2026 THEN should return 20 days`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = allWeekdays
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(20, result.getOrNull()?.requiredDays)
    }

    @Test
    fun `GIVEN 2 days per week required WHEN calculating for Feb 2026 THEN should return 8 days`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 2,
            requiredHoursPerWeek = 16f,
            weekdayPreferences = allWeekdays
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(8, result.getOrNull()?.requiredDays)
    }

    @Test
    fun `GIVEN 3 days per week required WHEN calculating for Feb 2026 THEN should return 12 days`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = allWeekdays
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(12, result.getOrNull()?.requiredDays)
    }

    @Test
    fun `GIVEN 2 weekday holidays in month WHEN calculating requirements THEN should reduce workdays count`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = allWeekdays
        )
        val holidays = listOf(
            Holiday(date = LocalDate.of(2026, 2, 16), description = "Holiday", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 17), description = "Holiday 2", type = HolidayType.PUBLIC_HOLIDAY)
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns holidays

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(18, result.getOrNull()?.requiredDays)
        assertEquals(2, result.getOrNull()?.holidaysCount)
    }

    @Test
    fun `GIVEN weekend holidays WHEN calculating requirements THEN should not affect required days`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = allWeekdays
        )
        val holidays = listOf(
            Holiday(date = LocalDate.of(2026, 2, 14), description = "Sat Holiday", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 15), description = "Sun Holiday", type = HolidayType.PUBLIC_HOLIDAY)
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns holidays

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        assertEquals(20, result.getOrNull()?.requiredDays)
    }

    @Test
    fun `GIVEN 8 hours per day with 2 days per week WHEN calculating THEN hours should be proportional`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 2,
            requiredHoursPerWeek = 16f,
            weekdayPreferences = allWeekdays
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns emptyList()

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val requirements = result.getOrNull()!!
        assertEquals(64f, requirements.requiredHours, 0.01f)
    }

    @Test
    fun `GIVEN holidays reducing weekdays WHEN calculating hours THEN should use raw days not ceiling`() = runTest {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 2,
            requiredHoursPerWeek = 16f,
            weekdayPreferences = allWeekdays
        )
        val holidays = listOf(
            Holiday(date = LocalDate.of(2026, 2, 16), description = "Holiday 1", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 17), description = "Holiday 2", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 18), description = "Holiday 3", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 19), description = "Holiday 4", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 20), description = "Holiday 5", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 23), description = "Holiday 6", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 24), description = "Holiday 7", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 25), description = "Holiday 8", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 26), description = "Holiday 9", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 27), description = "Holiday 10", type = HolidayType.PUBLIC_HOLIDAY),
            Holiday(date = LocalDate.of(2026, 2, 2), description = "Holiday 11", type = HolidayType.PUBLIC_HOLIDAY)
        )
        coEvery { repository.getSettingsOnce() } returns settings
        coEvery { repository.getHolidaysInRangeOnce(any(), any()) } returns holidays

        val result = useCase(YearMonth.of(2026, 2))

        assertTrue(result.isSuccess)
        val requirements = result.getOrNull()!!
        assertEquals(9, requirements.totalWeekdaysInMonth)
        assertEquals(4, requirements.requiredDays)
        assertEquals(28.8f, requirements.requiredHours, 0.01f)
    }
}
