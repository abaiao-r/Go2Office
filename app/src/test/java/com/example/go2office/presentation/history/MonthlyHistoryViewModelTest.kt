package com.example.go2office.presentation.history

import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.model.MonthProgress
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.domain.usecase.GetDailyEntriesUseCase
import com.example.go2office.domain.usecase.GetMonthProgressUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class MonthlyHistoryViewModelTest {

    private lateinit var getDailyEntries: GetDailyEntriesUseCase
    private lateinit var getMonthProgress: GetMonthProgressUseCase
    private lateinit var repository: OfficeRepository
    private lateinit var viewModel: MonthlyHistoryViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val testMonth = YearMonth.of(2026, 2)

    private val testEntries = listOf(
        DailyEntry(date = LocalDate.of(2026, 2, 10), wasInOffice = true, hoursWorked = 8f),
        DailyEntry(date = LocalDate.of(2026, 2, 11), wasInOffice = true, hoursWorked = 7.5f),
        DailyEntry(date = LocalDate.of(2026, 2, 12), wasInOffice = false, hoursWorked = 0f),
        DailyEntry(date = LocalDate.of(2026, 2, 13), wasInOffice = true, hoursWorked = 9f)
    )

    private val testProgress = MonthProgress(
        yearMonth = testMonth,
        requiredDays = 8,
        completedDays = 3,
        requiredHours = 64f,
        completedHours = 24.5f
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDailyEntries = mockk()
        getMonthProgress = mockk()
        repository = mockk()

        every { getDailyEntries.forMonth(any()) } returns flowOf(testEntries)
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = MonthlyHistoryViewModel(getDailyEntries, getMonthProgress, repository)
    }

    @Test
    fun `GIVEN entries exist WHEN viewModel initializes THEN should load entries`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.dailyEntries.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN entries exist WHEN viewModel initializes THEN should calculate total days worked`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.totalDaysWorked) // Only wasInOffice = true
    }

    @Test
    fun `GIVEN entries exist WHEN viewModel initializes THEN should calculate total hours worked`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(24.5f, viewModel.uiState.value.totalHoursWorked, 0.01f) // 8 + 7.5 + 9
    }

    @Test
    fun `GIVEN progress loaded WHEN viewModel initializes THEN should set required values`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(8, viewModel.uiState.value.requiredDays)
        assertEquals(64f, viewModel.uiState.value.requiredHours, 0.01f)
    }

    @Test
    fun `GIVEN entries loaded WHEN viewModel initializes THEN entries should be sorted by date descending`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val entries = viewModel.uiState.value.dailyEntries
        assertTrue(entries[0].date.isAfter(entries[1].date))
        assertTrue(entries[1].date.isAfter(entries[2].date))
    }

    @Test
    fun `GIVEN new month WHEN SelectMonth event THEN should reload data for new month`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val newMonth = YearMonth.of(2026, 3)
        viewModel.onEvent(MonthlyHistoryEvent.SelectMonth(newMonth))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newMonth, viewModel.uiState.value.selectedMonth)
    }

    @Test
    fun `GIVEN Refresh event WHEN triggered THEN should reload data`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(MonthlyHistoryEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN no entries WHEN viewModel initializes THEN totals should be zero`() = runTest {
        every { getDailyEntries.forMonth(any()) } returns flowOf(emptyList())

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.totalDaysWorked)
        assertEquals(0f, viewModel.uiState.value.totalHoursWorked, 0.01f)
    }

    @Test
    fun `GIVEN progress fetch fails WHEN viewModel initializes THEN required values should be zero`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.failure(Exception("Error"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.requiredDays)
        assertEquals(0f, viewModel.uiState.value.requiredHours, 0.01f)
    }

    @Test
    fun `GIVEN exception thrown WHEN loading data THEN should show error message`() = runTest {
        every { getDailyEntries.forMonth(any()) } throws RuntimeException("Database error")

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Database error", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}

