package com.example.go2office.data.mapper

import com.example.go2office.data.local.entities.OfficeSettingsEntity
import com.example.go2office.domain.model.OfficeSettings
import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant

class SettingsMapperTest {

    private val allWeekdays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    @Test
    fun `GIVEN settings entity WHEN mapping to domain THEN should preserve all fields`() {
        val entity = OfficeSettingsEntity(
            id = 1,
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = allWeekdays,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val domain = SettingsMapper.toDomain(entity)

        assertEquals(3, domain.requiredDaysPerWeek)
        assertEquals(24f, domain.requiredHoursPerWeek, 0.01f)
        assertEquals(allWeekdays, domain.weekdayPreferences)
    }

    @Test
    fun `GIVEN settings domain WHEN mapping to entity THEN should have id 1`() {
        val domain = OfficeSettings(
            requiredDaysPerWeek = 2,
            requiredHoursPerWeek = 16f,
            weekdayPreferences = allWeekdays
        )

        val entity = SettingsMapper.toEntity(domain)

        assertEquals(1, entity.id)
        assertEquals(2, entity.requiredDaysPerWeek)
        assertEquals(16f, entity.requiredHoursPerWeek, 0.01f)
    }

    @Test
    fun `GIVEN settings domain WHEN mapping to entity THEN should set timestamps`() {
        val domain = OfficeSettings(
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = allWeekdays
        )
        val beforeMapping = Instant.now()

        val entity = SettingsMapper.toEntity(domain)

        assertTrue(entity.createdAt.isAfter(beforeMapping.minusSeconds(1)))
        assertTrue(entity.updatedAt.isAfter(beforeMapping.minusSeconds(1)))
    }

    @Test
    fun `GIVEN entity with custom preferences WHEN mapping to domain THEN should preserve order`() {
        val customOrder = listOf(
            DayOfWeek.WEDNESDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.THURSDAY
        )
        val entity = OfficeSettingsEntity(
            id = 1,
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = customOrder,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val domain = SettingsMapper.toDomain(entity)

        assertEquals(customOrder, domain.weekdayPreferences)
        assertEquals(DayOfWeek.WEDNESDAY, domain.weekdayPreferences[0])
    }

    @Test
    fun `GIVEN existing entity WHEN updating with new domain THEN should preserve id and createdAt`() {
        val originalCreatedAt = Instant.now().minusSeconds(3600)
        val existingEntity = OfficeSettingsEntity(
            id = 1,
            requiredDaysPerWeek = 3,
            requiredHoursPerWeek = 24f,
            weekdayPreferences = allWeekdays,
            createdAt = originalCreatedAt,
            updatedAt = originalCreatedAt
        )
        val updatedDomain = OfficeSettings(
            requiredDaysPerWeek = 4,
            requiredHoursPerWeek = 32f,
            weekdayPreferences = allWeekdays
        )

        val updatedEntity = SettingsMapper.toEntityUpdate(updatedDomain, existingEntity)

        assertEquals(1, updatedEntity.id)
        assertEquals(originalCreatedAt, updatedEntity.createdAt)
        assertEquals(4, updatedEntity.requiredDaysPerWeek)
        assertEquals(32f, updatedEntity.requiredHoursPerWeek, 0.01f)
        assertTrue(updatedEntity.updatedAt.isAfter(originalCreatedAt))
    }

    @Test
    fun `GIVEN domain with 5 days WHEN mapping THEN entity should have 5 days`() {
        val domain = OfficeSettings(
            requiredDaysPerWeek = 5,
            requiredHoursPerWeek = 40f,
            weekdayPreferences = allWeekdays
        )

        val entity = SettingsMapper.toEntity(domain)

        assertEquals(5, entity.requiredDaysPerWeek)
    }

    @Test
    fun `GIVEN domain with 1 day WHEN mapping THEN entity should have 1 day`() {
        val domain = OfficeSettings(
            requiredDaysPerWeek = 1,
            requiredHoursPerWeek = 8f,
            weekdayPreferences = allWeekdays
        )

        val entity = SettingsMapper.toEntity(domain)

        assertEquals(1, entity.requiredDaysPerWeek)
    }
}

