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

class UpdateDailyHoursUseCaseTest {

    private lateinit var repository: OfficeRepository
    private lateinit var useCase: UpdateDailyHoursUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpdateDailyHoursUseCase(repository)
    }

    @Test
    fun `GIVEN valid hours WHEN updating THEN should save successfully`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        val result = useCase(date, 8.5f)

        assertTrue(result.isSuccess)
        coVerify { repository.saveDailyEntry(match { it.hoursWorked == 8.5f }) }
    }

    @Test
    fun `GIVEN negative hours WHEN updating THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 2, 10)

        val result = useCase(date, -5f)

        assertTrue(result.isFailure)
        assertEquals("Hours must be between 0 and 24", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN hours over 24 WHEN updating THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 2, 10)

        val result = useCase(date, 30f)

        assertTrue(result.isFailure)
        assertEquals("Hours must be between 0 and 24", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN wasInOffice true WHEN updating THEN should set flag correctly`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        useCase(date, 8f, wasInOffice = true)

        coVerify { repository.saveDailyEntry(match { it.wasInOffice == true }) }
    }

    @Test
    fun `GIVEN wasInOffice false WHEN updating THEN should set flag correctly`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        useCase(date, 0f, wasInOffice = false)

        coVerify { repository.saveDailyEntry(match { it.wasInOffice == false }) }
    }

    @Test
    fun `GIVEN notes provided WHEN updating THEN should include notes`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        val notes = "Updated hours"
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        useCase(date, 9f, notes = notes)

        coVerify { repository.saveDailyEntry(match { it.notes == notes }) }
    }

    @Test
    fun `GIVEN repository fails WHEN updating THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } throws RuntimeException("Database error")

        val result = useCase(date, 8f)

        assertTrue(result.isFailure)
    }
}

