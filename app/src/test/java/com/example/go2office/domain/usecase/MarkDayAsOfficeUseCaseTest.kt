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

class MarkDayAsOfficeUseCaseTest {

    private lateinit var repository: OfficeRepository
    private lateinit var useCase: MarkDayAsOfficeUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = MarkDayAsOfficeUseCase(repository)
    }

    @Test
    fun `GIVEN valid date and hours WHEN marking as office THEN should save successfully`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        val result = useCase(date, 8f)

        assertTrue(result.isSuccess)
        coVerify { repository.saveDailyEntry(match { it.date == date && it.hoursWorked == 8f }) }
    }

    @Test
    fun `GIVEN negative hours WHEN marking as office THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 2, 10)

        val result = useCase(date, -1f)

        assertTrue(result.isFailure)
        assertEquals("Hours must be between 0 and 24", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN hours over 24 WHEN marking as office THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 2, 10)

        val result = useCase(date, 25f)

        assertTrue(result.isFailure)
        assertEquals("Hours must be between 0 and 24", result.exceptionOrNull()?.message)
    }

    @Test
    fun `GIVEN 0 hours WHEN marking as office THEN should save successfully`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        val result = useCase(date, 0f)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `GIVEN 24 hours WHEN marking as office THEN should save successfully`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        val result = useCase(date, 24f)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `GIVEN date with notes WHEN marking as office THEN should include notes`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        val notes = "Team meeting day"
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        val result = useCase(date, 8f, notes)

        assertTrue(result.isSuccess)
        coVerify { repository.saveDailyEntry(match { it.notes == notes }) }
    }

    @Test
    fun `GIVEN repository fails WHEN marking as office THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } throws RuntimeException("Database error")

        val result = useCase(date, 8f)

        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN valid entry WHEN marking THEN wasInOffice should be true`() = runTest {
        val date = LocalDate.of(2026, 2, 10)
        coEvery { repository.saveDailyEntry(any()) } returns Result.success(Unit)

        useCase(date, 8f)

        coVerify { repository.saveDailyEntry(match { it.wasInOffice == true }) }
    }
}

