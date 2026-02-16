package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.go2office.presentation.onboarding.OnboardingScreen
import com.example.go2office.presentation.onboarding.OnboardingUiState
import com.example.go2office.presentation.onboarding.OnboardingViewModel
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
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultWeekdays = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    )

    @Test
    fun `GIVEN onboarding screen WHEN loaded THEN should show title`() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("Setup Go2Office").assertIsDisplayed()
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
        composeTestRule.onNode(hasContentDescription("Back")).assertIsDisplayed()
    }
}
