package com.example.go2office.presentation.settings

import com.example.go2office.domain.model.OfficeSettings
import com.example.go2office.domain.usecase.GetOfficeSettingsUseCase
import com.example.go2office.domain.usecase.SaveOfficeSettingsUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var getSettings: GetOfficeSettingsUseCase
    private lateinit var saveSettings: SaveOfficeSettingsUseCase
    private lateinit var viewModel: SettingsViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val allWeekdays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    private val testSettings = OfficeSettings(
        requiredDaysPerWeek = 3,
        requiredHoursPerWeek = 24f,
        weekdayPreferences = allWeekdays
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSettings = mockk()
        saveSettings = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN settings exist WHEN viewModel initializes THEN should load settings`() = runTest {
        every { getSettings() } returns flowOf(testSettings)

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testSettings, viewModel.uiState.value.settings)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `GIVEN no settings WHEN viewModel initializes THEN settings should be null`() = runTest {
        every { getSettings() } returns flowOf(null)

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.settings)
    }

    @Test
    fun `GIVEN new settings WHEN UpdateSettings event THEN should update state`() = runTest {
        every { getSettings() } returns flowOf(testSettings)

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        val newSettings = OfficeSettings(
            requiredDaysPerWeek = 4,
            requiredHoursPerWeek = 32f,
            weekdayPreferences = allWeekdays
        )

        viewModel.onEvent(SettingsEvent.UpdateSettings(newSettings))

        assertEquals(4, viewModel.uiState.value.settings?.requiredDaysPerWeek)
        assertEquals(32f, viewModel.uiState.value.settings?.requiredHoursPerWeek)
    }

    @Test
    fun `GIVEN valid settings WHEN Save event THEN should call saveSettings`() = runTest {
        every { getSettings() } returns flowOf(testSettings)
        coEvery { saveSettings(any()) } returns Result.success(Unit)

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(SettingsEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { saveSettings(testSettings) }
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `GIVEN null settings WHEN Save event THEN should show error`() = runTest {
        every { getSettings() } returns flowOf(null)

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(SettingsEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("No settings to save", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `GIVEN save fails WHEN Save event THEN should show error message`() = runTest {
        every { getSettings() } returns flowOf(testSettings)
        coEvery { saveSettings(any()) } returns Result.failure(Exception("Database error"))

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(SettingsEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Database error", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `GIVEN error message showing WHEN DismissError event THEN should clear error`() = runTest {
        every { getSettings() } returns flowOf(null)

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(SettingsEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onEvent(SettingsEvent.DismissError)

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `GIVEN settings loaded WHEN updating days THEN should preserve other settings`() = runTest {
        every { getSettings() } returns flowOf(testSettings)

        viewModel = SettingsViewModel(getSettings, saveSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedSettings = testSettings.copy(requiredDaysPerWeek = 5)
        viewModel.onEvent(SettingsEvent.UpdateSettings(updatedSettings))

        assertEquals(5, viewModel.uiState.value.settings?.requiredDaysPerWeek)
        assertEquals(24f, viewModel.uiState.value.settings?.requiredHoursPerWeek)
        assertEquals(allWeekdays, viewModel.uiState.value.settings?.weekdayPreferences)
    }
}

