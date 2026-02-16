package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.go2office.domain.model.OfficeLocation
import com.example.go2office.presentation.autodetection.AutoDetectionScreen
import com.example.go2office.presentation.autodetection.AutoDetectionUiState
import com.example.go2office.presentation.autodetection.AutoDetectionViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutoDetectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testLocation = OfficeLocation(
        latitude = 38.7223,
        longitude = -9.1393,
        radiusMeters = 100f,
        name = "Main Office"
    )

    @Test
    fun GIVEN_auto_detection_screen_WHEN_loaded_THEN_should_show_title() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState())
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Auto-Detection").assertIsDisplayed()
    }

    @Test
    fun GIVEN_back_button_WHEN_clicked_THEN_should_navigate_back() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState())
        var navigatedBack = false
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = { navigatedBack = true }) }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack)
    }

    @Test
    fun GIVEN_detection_disabled_WHEN_loaded_THEN_should_show_disabled_status() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState(isEnabled = false))
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Disabled", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_detection_enabled_WHEN_loaded_THEN_should_show_enabled_status() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState(isEnabled = true, isGeofencingActive = true))
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Enabled", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_office_location_set_WHEN_loaded_THEN_should_show_location_name() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState(officeLocation = testLocation))
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Main Office", substring = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_permissions_section_WHEN_displayed_THEN_should_show_permissions_card() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState())
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Permissions", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_use_current_location_WHEN_displayed_THEN_should_be_visible() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState())
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Use Current Location", substring = true, ignoreCase = true).assertIsDisplayed()
    }
}

