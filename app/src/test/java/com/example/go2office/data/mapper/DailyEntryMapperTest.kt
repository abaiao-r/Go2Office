package com.example.go2office.data.mapper

import com.example.go2office.data.local.entities.DailyEntryEntity
import com.example.go2office.domain.model.DailyEntry
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class DailyEntryMapperTest {

    @Test
    fun `GIVEN office day entity WHEN mapping to domain THEN should preserve all fields`() {
        val entity = DailyEntryEntity(
            id = 1L,
            date = LocalDate.of(2026, 2, 16),
            wasInOffice = true,
            hoursWorked = 8.5f,
            notes = "Normal day",
            createdAt = Instant.now()
        )

        val domain = DailyEntryMapper.toDomain(entity)

        assertEquals(1L, domain.id)
        assertEquals(LocalDate.of(2026, 2, 16), domain.date)
        assertTrue(domain.wasInOffice)
        assertEquals(8.5f, domain.hoursWorked, 0.01f)
        assertEquals("Normal day", domain.notes)
    }

    @Test
    fun `GIVEN non-office day entity WHEN mapping to domain THEN wasInOffice should be false`() {
        val entity = DailyEntryEntity(
            id = 2L,
            date = LocalDate.of(2026, 2, 17),
            wasInOffice = false,
            hoursWorked = 0f,
            notes = null,
            createdAt = Instant.now()
        )

        val domain = DailyEntryMapper.toDomain(entity)

        assertFalse(domain.wasInOffice)
        assertEquals(0f, domain.hoursWorked, 0.01f)
        assertNull(domain.notes)
    }

    @Test
    fun `GIVEN office day domain WHEN mapping to entity THEN should preserve all fields`() {
        val domain = DailyEntry(
            id = 3L,
            date = LocalDate.of(2026, 2, 18),
            wasInOffice = true,
            hoursWorked = 7.25f,
            notes = "Short day"
        )

        val entity = DailyEntryMapper.toEntity(domain)

        assertEquals(3L, entity.id)
        assertEquals(LocalDate.of(2026, 2, 18), entity.date)
        assertTrue(entity.wasInOffice)
        assertEquals(7.25f, entity.hoursWorked, 0.01f)
        assertEquals("Short day", entity.notes)
    }

    @Test
    fun `GIVEN domain with null notes WHEN mapping to entity THEN notes should be null`() {
        val domain = DailyEntry(
            date = LocalDate.of(2026, 2, 19),
            wasInOffice = true,
            hoursWorked = 8f,
            notes = null
        )

        val entity = DailyEntryMapper.toEntity(domain)

        assertNull(entity.notes)
    }

    @Test
    fun `GIVEN entity with 10 hours WHEN mapping THEN should preserve capped hours`() {
        val entity = DailyEntryEntity(
            id = 4L,
            date = LocalDate.of(2026, 2, 20),
            wasInOffice = true,
            hoursWorked = 10f,
            notes = "Long day (capped)",
            createdAt = Instant.now()
        )

        val domain = DailyEntryMapper.toDomain(entity)

        assertEquals(10f, domain.hoursWorked, 0.01f)
    }

    @Test
    fun `GIVEN list of entities WHEN mapping to domain list THEN should map all correctly`() {
        val entities = listOf(
            DailyEntryEntity(id = 1L, date = LocalDate.of(2026, 2, 16), wasInOffice = true, hoursWorked = 8f, createdAt = Instant.now()),
            DailyEntryEntity(id = 2L, date = LocalDate.of(2026, 2, 17), wasInOffice = false, hoursWorked = 0f, createdAt = Instant.now()),
            DailyEntryEntity(id = 3L, date = LocalDate.of(2026, 2, 18), wasInOffice = true, hoursWorked = 9f, createdAt = Instant.now())
        )

        val domainList = DailyEntryMapper.toDomainList(entities)

        assertEquals(3, domainList.size)
        assertTrue(domainList[0].wasInOffice)
        assertFalse(domainList[1].wasInOffice)
        assertTrue(domainList[2].wasInOffice)
    }

    @Test
    fun `GIVEN empty entity list WHEN mapping to domain list THEN should return empty list`() {
        val domainList = DailyEntryMapper.toDomainList(emptyList())

        assertTrue(domainList.isEmpty())
    }

    @Test
    fun `GIVEN domain with default id WHEN mapping to entity THEN id should be 0`() {
        val domain = DailyEntry(
            date = LocalDate.of(2026, 2, 21),
            wasInOffice = true,
            hoursWorked = 8f
        )

        val entity = DailyEntryMapper.toEntity(domain)

        assertEquals(0L, entity.id)
    }

    @Test
    fun `GIVEN entity WHEN mapping round trip THEN data should be preserved`() {
        val original = DailyEntryEntity(
            id = 100L,
            date = LocalDate.of(2026, 3, 1),
            wasInOffice = true,
            hoursWorked = 8.75f,
            notes = "Test notes",
            createdAt = Instant.now()
        )

        val domain = DailyEntryMapper.toDomain(original)
        val backToEntity = DailyEntryMapper.toEntity(domain)

        assertEquals(original.id, backToEntity.id)
        assertEquals(original.date, backToEntity.date)
        assertEquals(original.wasInOffice, backToEntity.wasInOffice)
        assertEquals(original.hoursWorked, backToEntity.hoursWorked, 0.01f)
        assertEquals(original.notes, backToEntity.notes)
    }
}

