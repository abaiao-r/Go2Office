package com.example.go2office.data.mapper

import com.example.go2office.data.local.entities.HolidayEntity
import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class HolidayMapperTest {

    @Test
    fun `GIVEN public holiday entity WHEN mapping to domain THEN should return correct holiday type`() {
        val entity = HolidayEntity(
            id = 1L,
            date = LocalDate.of(2026, 2, 16),
            description = "National Day",
            type = "PUBLIC_HOLIDAY",
            createdAt = Instant.now()
        )

        val domain = HolidayMapper.toDomain(entity)

        assertEquals(HolidayType.PUBLIC_HOLIDAY, domain.type)
        assertEquals("National Day", domain.description)
        assertEquals(LocalDate.of(2026, 2, 16), domain.date)
    }

    @Test
    fun `GIVEN vacation entity WHEN mapping to domain THEN should return vacation type`() {
        val entity = HolidayEntity(
            id = 2L,
            date = LocalDate.of(2026, 8, 15),
            description = "Summer vacation",
            type = "VACATION",
            createdAt = Instant.now()
        )

        val domain = HolidayMapper.toDomain(entity)

        assertEquals(HolidayType.VACATION, domain.type)
        assertEquals("Summer vacation", domain.description)
    }

    @Test
    fun `GIVEN unknown type entity WHEN mapping to domain THEN should default to public holiday`() {
        val entity = HolidayEntity(
            id = 3L,
            date = LocalDate.of(2026, 5, 1),
            description = "Unknown holiday",
            type = "UNKNOWN_TYPE",
            createdAt = Instant.now()
        )

        val domain = HolidayMapper.toDomain(entity)

        assertEquals(HolidayType.PUBLIC_HOLIDAY, domain.type)
    }

    @Test
    fun `GIVEN public holiday domain WHEN mapping to entity THEN should return correct type string`() {
        val domain = Holiday(
            id = 1L,
            date = LocalDate.of(2026, 4, 25),
            description = "Freedom Day",
            type = HolidayType.PUBLIC_HOLIDAY
        )

        val entity = HolidayMapper.toEntity(domain)

        assertEquals("PUBLIC_HOLIDAY", entity.type)
        assertEquals("Freedom Day", entity.description)
        assertEquals(LocalDate.of(2026, 4, 25), entity.date)
    }

    @Test
    fun `GIVEN vacation domain WHEN mapping to entity THEN should return vacation string`() {
        val domain = Holiday(
            id = 2L,
            date = LocalDate.of(2026, 7, 1),
            description = "Personal vacation",
            type = HolidayType.VACATION
        )

        val entity = HolidayMapper.toEntity(domain)

        assertEquals("VACATION", entity.type)
    }

    @Test
    fun `GIVEN entity id WHEN mapping to domain and back THEN should preserve id`() {
        val entity = HolidayEntity(
            id = 42L,
            date = LocalDate.of(2026, 12, 25),
            description = "Christmas",
            type = "PUBLIC_HOLIDAY",
            createdAt = Instant.now()
        )

        val domain = HolidayMapper.toDomain(entity)
        val backToEntity = HolidayMapper.toEntity(domain)

        assertEquals(42L, backToEntity.id)
    }

    @Test
    fun `GIVEN list of entities WHEN mapping to domain list THEN should map all correctly`() {
        val entities = listOf(
            HolidayEntity(id = 1L, date = LocalDate.of(2026, 1, 1), description = "New Year", type = "PUBLIC_HOLIDAY", createdAt = Instant.now()),
            HolidayEntity(id = 2L, date = LocalDate.of(2026, 8, 1), description = "Vacation", type = "VACATION", createdAt = Instant.now()),
            HolidayEntity(id = 3L, date = LocalDate.of(2026, 12, 25), description = "Christmas", type = "PUBLIC_HOLIDAY", createdAt = Instant.now())
        )

        val domainList = HolidayMapper.toDomainList(entities)

        assertEquals(3, domainList.size)
        assertEquals(HolidayType.PUBLIC_HOLIDAY, domainList[0].type)
        assertEquals(HolidayType.VACATION, domainList[1].type)
        assertEquals(HolidayType.PUBLIC_HOLIDAY, domainList[2].type)
    }

    @Test
    fun `GIVEN empty entity list WHEN mapping to domain list THEN should return empty list`() {
        val domainList = HolidayMapper.toDomainList(emptyList())

        assertTrue(domainList.isEmpty())
    }
}

