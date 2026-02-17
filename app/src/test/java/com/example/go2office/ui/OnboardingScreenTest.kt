package com.example.go2office.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.go2office.R
import com.example.go2office.presentation.onboarding.OnboardingScreen
import com.example.go2office.presentation.onboarding.OnboardingUiState
import com.example.go2office.presentation.onboarding.OnboardingViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    private val defaultWeekdays = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    )

    @Test
    fun `GIVEN onboarding screen WHEN loaded THEN should show title`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText(context.getString(R.string.setup_title)).assertIsDisplayed()
    }

    @Test
    fun `GIVEN step one WHEN loaded THEN should show step indicator`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("Step 1 of 5").assertIsDisplayed()
    }

    @Test
    fun `GIVEN step one WHEN loaded THEN should show required office days title`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("Required Office Days", substring = true).assertIsDisplayed()
    }

    @Test
    fun `GIVEN next button WHEN displayed THEN should be visible`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun `GIVEN step greater than zero WHEN loaded THEN should show back button`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 1, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNode(hasContentDescription(context.getString(R.string.back))).assertIsDisplayed()
    }

    @Test
    fun `GIVEN auto detection step with auto detection enabled WHEN loaded THEN should show pick on map button`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            OnboardingUiState(
                currentStep = 3,
                weekdayPreferences = defaultWeekdays,
                enableAutoDetection = true
            )
        )
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("🗺️ Pick on Map", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `GIVEN auto detection step WHEN pick on map clicked THEN should navigate to map picker`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            OnboardingUiState(
                currentStep = 3,
                weekdayPreferences = defaultWeekdays,
                enableAutoDetection = true,
                hasLocationPermission = true
            )
        )
        var navigatedToMapPicker = false
        composeTestRule.setContent {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {},
                onNavigateToMapPicker = { _, _ -> navigatedToMapPicker = true }
            )
        }
        composeTestRule.onNodeWithText("🗺️ Pick on Map", useUnmergedTree = true).performClick()
        assertTrue(navigatedToMapPicker)
    }

    @Test
    fun `GIVEN auto detection step with location set WHEN loaded THEN should show location name`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            OnboardingUiState(
                currentStep = 3,
                weekdayPreferences = defaultWeekdays,
                enableAutoDetection = true,
                officeLatitude = 38.7223,
                officeLongitude = -9.1393,
                officeName = "My Office"
            )
        )
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("My Office", substring = true, useUnmergedTree = true).assertExists()
    }

    @Test
    fun `GIVEN auto detection disabled WHEN loaded THEN should not show pick on map button`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            OnboardingUiState(
                currentStep = 3,
                weekdayPreferences = defaultWeekdays,
                enableAutoDetection = false
            )
        )
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("🗺️ Pick on Map", useUnmergedTree = true).assertDoesNotExist()
    }
}
