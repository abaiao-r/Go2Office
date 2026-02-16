package com.example.go2office.presentation.dayentry

import androidx.lifecycle.SavedStateHandle
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.domain.usecase.GetDailyEntriesUseCase
import com.example.go2office.domain.usecase.UpdateDailyHoursUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DayEntryViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getDailyEntries: GetDailyEntriesUseCase
    private lateinit var updateDailyHours: UpdateDailyHoursUseCase
    private lateinit var repository: OfficeRepository
    private lateinit var viewModel: DayEntryViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val testDate = LocalDate.of(2026, 2, 16)

    private val existingEntry = DailyEntry(
        date = testDate,
        wasInOffice = true,
        hoursWorked = 7.5f,
        notes = "Meeting day"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = mockk()
        getDailyEntries = mockk()
        updateDailyHours = mockk()
        repository = mockk()

        every { savedStateHandle.get<String>("date") } returns testDate.toString()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = DayEntryViewModel(
            savedStateHandle,
            getDailyEntries,
            updateDailyHours,
            repository
        )
    }

    @Test
    fun `GIVEN existing entry WHEN viewModel initializes THEN should load entry data`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns existingEntry

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testDate, viewModel.uiState.value.selectedDate)
        assertTrue(viewModel.uiState.value.wasInOffice)
        assertEquals(7.5f, viewModel.uiState.value.hoursWorked, 0.01f)
        assertEquals("Meeting day", viewModel.uiState.value.notes)
        assertTrue(viewModel.uiState.value.isExistingEntry)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN no existing entry WHEN viewModel initializes THEN should use defaults`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testDate, viewModel.uiState.value.selectedDate)
        assertFalse(viewModel.uiState.value.isExistingEntry)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN new date WHEN SelectDate event THEN should reload entry for new date`() = runTest {
        coEvery { getDailyEntries.getByDate(any()) } returns null

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val newDate = LocalDate.of(2026, 2, 17)
        viewModel.onEvent(DayEntryEvent.SelectDate(newDate))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newDate, viewModel.uiState.value.selectedDate)
        coVerify { getDailyEntries.getByDate(newDate) }
    }

    @Test
    fun `GIVEN wasInOffice toggle WHEN ToggleWasInOffice event THEN should update state`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.ToggleWasInOffice(false))

        assertFalse(viewModel.uiState.value.wasInOffice)
    }

    @Test
    fun `GIVEN new hours WHEN UpdateHours event THEN should update state`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.UpdateHours(9.5f))

        assertEquals(9.5f, viewModel.uiState.value.hoursWorked, 0.01f)
    }

    @Test
    fun `GIVEN new notes WHEN UpdateNotes event THEN should update state`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.UpdateNotes("New note"))

        assertEquals("New note", viewModel.uiState.value.notes)
    }

    @Test
    fun `GIVEN valid entry WHEN Save event THEN should call updateDailyHours`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null
        coEvery { updateDailyHours(any(), any(), any(), any()) } returns Result.success(Unit)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.UpdateHours(8f))
        viewModel.onEvent(DayEntryEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDailyHours(testDate, 8f, any(), any()) }
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `GIVEN save fails WHEN Save event THEN should show error`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null
        coEvery { updateDailyHours(any(), any(), any(), any()) } returns Result.failure(Exception("Save failed"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Save failed", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `GIVEN existing entry WHEN Delete event THEN should call repository delete`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns existingEntry
        coEvery { repository.deleteDailyEntry(testDate) } returns Result.success(Unit)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.Delete)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteDailyEntry(testDate) }
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `GIVEN delete fails WHEN Delete event THEN should show error`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns existingEntry
        coEvery { repository.deleteDailyEntry(testDate) } returns Result.failure(Exception("Delete failed"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.Delete)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Delete failed", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `GIVEN error showing WHEN DismissError event THEN should clear error`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null
        coEvery { updateDailyHours(any(), any(), any(), any()) } returns Result.failure(Exception("Error"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onEvent(DayEntryEvent.DismissError)

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `GIVEN blank notes WHEN Save event THEN should pass null for notes`() = runTest {
        coEvery { getDailyEntries.getByDate(testDate) } returns null
        coEvery { updateDailyHours(any(), any(), any(), any()) } returns Result.success(Unit)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(DayEntryEvent.UpdateNotes("   "))
        viewModel.onEvent(DayEntryEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDailyHours(testDate, any(), any(), null) }
    }
}

