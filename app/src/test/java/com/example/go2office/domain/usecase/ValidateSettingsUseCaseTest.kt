package com.example.go2office.domain.usecase

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

class ValidateSettingsUseCaseTest {

    private lateinit var useCase: ValidateSettingsUseCase

    private val validPreferences = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    @Before
    fun setup() {
        useCase = ValidateSettingsUseCase()
    }

    @Test
    fun `GIVEN 0 days per week WHEN validating THEN should return failure`() {
        val result = useCase(
            requiredDaysPerWeek = 0,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN 6 days per week WHEN validating THEN should return failure`() {
        val result = useCase(
            requiredDaysPerWeek = 6,
            requiredHoursPerWeek = 48f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN 3 days per week with valid inputs WHEN validating THEN should return success`() {
        val result = useCase(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.requiredDaysPerWeek)
    }

    @Test
    fun `GIVEN 0 hours per week WHEN validating THEN should return failure`() {
        val result = useCase(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 0f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN negative hours per week WHEN validating THEN should return failure`() {
        val result = useCase(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = -10f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN empty preferences WHEN validating THEN should return failure`() {
        val result = useCase(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = emptyList()
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN 5 days and 40 hours per week WHEN validating THEN should return success`() {
        val result = useCase(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull()?.requiredDaysPerWeek)
        assertEquals(40f, result.getOrNull()?.requiredHoursPerWeek)
    }

    @Test
    fun `GIVEN 1 day per week minimum WHEN validating THEN should return success`() {
        val result = useCase(
            requiredDaysPerWeek = 1,
            requiredHoursPerWeek = 8f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `GIVEN hours over 40 WHEN validating THEN should return failure`() {
        val result = useCase(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 50f,
            weekdayPreferences = validPreferences
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN duplicate weekday preferences WHEN validating THEN should return failure`() {
        val duplicatePrefs = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        )
        val result = useCase(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = duplicatePrefs
        )
        assertTrue(result.isFailure)
    }
}
