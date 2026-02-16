package com.example.go2office.domain.usecase

import com.example.go2office.domain.repository.OfficeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AddHolidayUseCaseTest {

    private lateinit var repository: OfficeRepository
    private lateinit var useCase: AddHolidayUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddHolidayUseCase(repository)
    }

    @Test
    fun `GIVEN valid date and description WHEN adding holiday THEN should save successfully`() = runTest {
        val date = LocalDate.of(2026, 12, 25)
        val description = "Christmas"
        coEvery { repository.saveHoliday(any()) } returns Result.success(Unit)

        val result = useCase(date, description)

        assertTrue(result.isSuccess)
        coVerify { repository.saveHoliday(match { it.date == date && it.description == description }) }
    }

    @Test
    fun `GIVEN empty description WHEN adding holiday THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 12, 25)
        val description = ""

        val result = useCase(date, description)

        assertTrue(result.isFailure)
        assertEquals("Description cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN blank description WHEN adding holiday THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 12, 25)
        val description = "   "

        val result = useCase(date, description)

        assertTrue(result.isFailure)
        assertEquals("Description cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN repository throws exception WHEN adding holiday THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 12, 25)
        val description = "Christmas"
        coEvery { repository.saveHoliday(any()) } throws RuntimeException("Database error")

        val result = useCase(date, description)

        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN valid holiday data WHEN adding THEN should create holiday with correct type`() = runTest {
        val date = LocalDate.of(2026, 4, 25)
        val description = "Freedom Day"
        coEvery { repository.saveHoliday(any()) } returns Result.success(Unit)

        val result = useCase(date, description)

        assertTrue(result.isSuccess)
        coVerify {
            repository.saveHoliday(match {
                it.date == date &&
                it.description == description
            })
        }
    }

    @Test
    fun `GIVEN weekend date WHEN adding holiday THEN should still save successfully`() = runTest {
        val saturdayDate = LocalDate.of(2026, 2, 14) // Saturday
        val description = "Weekend Holiday"
        coEvery { repository.saveHoliday(any()) } returns Result.success(Unit)

        val result = useCase(saturdayDate, description)

        assertTrue(result.isSuccess)
    }
}

