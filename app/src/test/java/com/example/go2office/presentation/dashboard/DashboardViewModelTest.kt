package com.example.go2office.presentation.dashboard

import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.model.MonthProgress
import com.example.go2office.domain.model.OfficePresence
import com.example.go2office.domain.model.SuggestedDay
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.domain.usecase.GetActiveOfficeSessionUseCase
import com.example.go2office.domain.usecase.GetDailyEntriesUseCase
import com.example.go2office.domain.usecase.GetMonthProgressUseCase
import com.example.go2office.domain.usecase.GetSuggestedOfficeDaysUseCase
import com.example.go2office.domain.usecase.MarkDayAsOfficeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var getMonthProgress: GetMonthProgressUseCase
    private lateinit var getSuggestedDays: GetSuggestedOfficeDaysUseCase
    private lateinit var getDailyEntries: GetDailyEntriesUseCase
    private lateinit var markDayAsOffice: MarkDayAsOfficeUseCase
    private lateinit var getActiveSession: GetActiveOfficeSessionUseCase
    private lateinit var repository: OfficeRepository
    private lateinit var viewModel: DashboardViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testProgress = MonthProgress(
        yearMonth = YearMonth.of(2026, 2),
        requiredDays = 8,
        completedDays = 3,
        requiredHours = 64f,
        completedHours = 24f
    )

    private val testSuggestedDays = listOf(
        SuggestedDay(date = LocalDate.of(2026, 2, 16), dayOfWeek = DayOfWeek.MONDAY, reason = "Preferred day", priority = 1),
        SuggestedDay(date = LocalDate.of(2026, 2, 17), dayOfWeek = DayOfWeek.TUESDAY, reason = "Preferred day", priority = 2)
    )

    private val testEntries = listOf(
        DailyEntry(date = LocalDate.of(2026, 2, 10), wasInOffice = true, hoursWorked = 8f),
        DailyEntry(date = LocalDate.of(2026, 2, 11), wasInOffice = true, hoursWorked = 8f)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMonthProgress = mockk()
        getSuggestedDays = mockk()
        getDailyEntries = mockk()
        markDayAsOffice = mockk()
        getActiveSession = mockk()
        repository = mockk()

        every { getActiveSession() } returns flowOf(null)
        every { repository.getTodayTotalHours() } returns flowOf(0f)
        every { repository.getSettings() } returns flowOf(null)
        every { repository.getAllHolidays() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = DashboardViewModel(
            getMonthProgress,
            getSuggestedDays,
            getDailyEntries,
            markDayAsOffice,
            getActiveSession,
            repository
        )
    }

    @Test
    fun `GIVEN successful data load WHEN viewModel initializes THEN should update state with progress`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testProgress, viewModel.uiState.value.monthProgress)
        assertEquals(testSuggestedDays, viewModel.uiState.value.suggestedDays)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN progress load fails WHEN viewModel initializes THEN should show error`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.failure(Exception("Failed to load"))
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(emptyList())
        every { getDailyEntries.recent(any()) } returns flowOf(emptyList())

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Failed to load", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN user selects different month WHEN SelectMonth event THEN should reload data`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val newMonth = YearMonth.of(2026, 3)
        viewModel.onEvent(DashboardEvent.SelectMonth(newMonth))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newMonth, viewModel.uiState.value.selectedMonth)
    }

    @Test
    fun `GIVEN active session exists WHEN session updates THEN should set isAtOffice true`() = runTest {
        val activeSession = OfficePresence(
            id = 1,
            locationId = 1,
            entryTime = "2026-02-16T09:00:00",
            exitTime = null,
            totalHours = 0f
        )
        every { getActiveSession() } returns flowOf(activeSession)
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAtOffice)
        assertNotNull(viewModel.uiState.value.activeSession)
    }

    @Test
    fun `GIVEN no active session WHEN session updates THEN should set isAtOffice false`() = runTest {
        every { getActiveSession() } returns flowOf(null)
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAtOffice)
        assertNull(viewModel.uiState.value.activeSession)
    }

    @Test
    fun `GIVEN valid date and hours WHEN QuickMarkDay event THEN should mark day`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)
        coEvery { markDayAsOffice(any(), any(), any(), any()) } returns Result.success(Unit)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val date = LocalDate.of(2026, 2, 16)
        viewModel.onEvent(DashboardEvent.QuickMarkDay(date, 8f))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { markDayAsOffice(date, 8f, any(), any()) }
    }

    @Test
    fun `GIVEN mark day fails WHEN QuickMarkDay event THEN should show error`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)
        coEvery { markDayAsOffice(any(), any(), any(), any()) } returns Result.failure(Exception("Cannot mark future dates"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DashboardEvent.QuickMarkDay(LocalDate.of(2026, 2, 20), 8f))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Cannot mark future dates", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `GIVEN error showing WHEN DismissError event THEN should clear error`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.failure(Exception("Error"))
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(emptyList())
        every { getDailyEntries.recent(any()) } returns flowOf(emptyList())

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onEvent(DashboardEvent.DismissError)

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `GIVEN Refresh event WHEN triggered THEN should reload dashboard data`() = runTest {
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DashboardEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 2) { getMonthProgress(any(), any()) }
    }

    @Test
    fun `GIVEN today total hours updates WHEN collecting THEN should update state`() = runTest {
        every { repository.getTodayTotalHours() } returns flowOf(5.5f)
        coEvery { getMonthProgress(any(), any()) } returns Result.success(testProgress)
        coEvery { getSuggestedDays(any(), any(), any()) } returns Result.success(testSuggestedDays)
        every { getDailyEntries.recent(any()) } returns flowOf(testEntries)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(5.5f, viewModel.uiState.value.todayTotalHours, 0.01f)
    }
}
