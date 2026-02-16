package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.go2office.domain.model.OfficeSettings
import com.example.go2office.presentation.settings.SettingsScreen
import com.example.go2office.presentation.settings.SettingsUiState
import com.example.go2office.presentation.settings.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testSettings = OfficeSettings(
        requiredDaysPerWeek = 3,
        requiredHoursPerWeek = 24f,
        weekdayPreferences = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    )

    @Test
    fun `GIVEN settings loaded WHEN screen renders THEN should show title`() {
        val viewModel = mockk<SettingsViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(SettingsUiState(isLoading = false, settings = testSettings))
        composeTestRule.setContent { SettingsScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun `GIVEN back button WHEN clicked THEN should navigate back`() {
        val viewModel = mockk<SettingsViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(SettingsUiState(isLoading = false, settings = testSettings))
        var navigatedBack = false
        composeTestRule.setContent { SettingsScreen(viewModel = viewModel, onNavigateBack = { navigatedBack = true }) }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack)
    }

    @Test
    fun `GIVEN settings WHEN loaded THEN should show office requirements`() {
        val viewModel = mockk<SettingsViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(SettingsUiState(isLoading = false, settings = testSettings))
        composeTestRule.setContent { SettingsScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Office Requirements").assertIsDisplayed()
    }

    @Test
    fun `GIVEN auto detection card WHEN clicked THEN should navigate to auto detection`() {
        val viewModel = mockk<SettingsViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(SettingsUiState(isLoading = false, settings = testSettings))
        var navigated = false
        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel, onNavigateBack = {}, onNavigateToAutoDetection = { navigated = true })
        }
        composeTestRule.onNodeWithText("Auto-Detection", substring = true).performClick()
        assert(navigated)
    }
}

