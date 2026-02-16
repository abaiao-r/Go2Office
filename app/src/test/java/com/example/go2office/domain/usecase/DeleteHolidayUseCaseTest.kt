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

class DeleteHolidayUseCaseTest {

    private lateinit var repository: OfficeRepository
    private lateinit var useCase: DeleteHolidayUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteHolidayUseCase(repository)
    }

    @Test
    fun `GIVEN existing holiday WHEN deleting THEN should return success`() = runTest {
        val date = LocalDate.of(2026, 12, 25)
        coEvery { repository.deleteHoliday(date) } returns Result.success(Unit)

        val result = useCase(date)

        assertTrue(result.isSuccess)
        coVerify { repository.deleteHoliday(date) }
    }

    @Test
    fun `GIVEN repository fails WHEN deleting THEN should return failure`() = runTest {
        val date = LocalDate.of(2026, 12, 25)
        coEvery { repository.deleteHoliday(date) } returns Result.failure(RuntimeException("Database error"))

        val result = useCase(date)

        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN any date WHEN deleting THEN should call repository`() = runTest {
        val date = LocalDate.of(2026, 5, 1)
        coEvery { repository.deleteHoliday(date) } returns Result.success(Unit)

        useCase(date)

        coVerify { repository.deleteHoliday(date) }
    }
}

