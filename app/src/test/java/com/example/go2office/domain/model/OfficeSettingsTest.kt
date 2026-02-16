package com.example.go2office.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek

class OfficeSettingsTest {

    private val allWeekdays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    @Test
    fun `GIVEN valid settings WHEN creating THEN should succeed`() {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = allWeekdays
        )

        assertEquals(3, settings.requiredDaysPerWeek)
        assertEquals(24f, settings.requiredHoursPerWeek, 0.01f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN 0 days per week WHEN creating THEN should throw exception`() {
        OfficeSettings(
            requiredDaysPerWeek = 0,
            requiredHoursPerWeek = 0f,
            weekdayPreferences = allWeekdays
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN 6 days per week WHEN creating THEN should throw exception`() {
        OfficeSettings(
            requiredDaysPerWeek = 6,
            requiredHoursPerWeek = 48f,
            weekdayPreferences = allWeekdays
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN negative hours WHEN creating THEN should throw exception`() {
        OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = -10f,
            weekdayPreferences = allWeekdays
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN 0 hours WHEN creating THEN should throw exception`() {
        OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 0f,
            weekdayPreferences = allWeekdays
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN less than 5 preferences WHEN creating THEN should throw exception`() {
        OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN duplicate preferences WHEN creating THEN should throw exception`() {
        OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN weekend in preferences WHEN creating THEN should throw exception`() {
        OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.SATURDAY
            )
        )
    }

    @Test
    fun `GIVEN 1 day per week WHEN creating THEN should succeed`() {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 1,
            requiredHoursPerWeek = 8f,
            weekdayPreferences = allWeekdays
        )

        assertEquals(1, settings.requiredDaysPerWeek)
    }

    @Test
    fun `GIVEN 5 days per week WHEN creating THEN should succeed`() {
        val settings = OfficeSettings(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = allWeekdays
        )

        assertEquals(5, settings.requiredDaysPerWeek)
    }

    @Test
    fun `GIVEN custom preference order WHEN creating THEN should preserve order`() {
        val customOrder = listOf(
            DayOfWeek.FRIDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.MONDAY
        )
        val settings = OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = customOrder
        )

        assertEquals(DayOfWeek.FRIDAY, settings.weekdayPreferences[0])
        assertEquals(DayOfWeek.MONDAY, settings.weekdayPreferences[4])
    }
}

