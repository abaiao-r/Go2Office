package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.go2office.presentation.onboarding.OnboardingScreen
import com.example.go2office.presentation.onboarding.OnboardingUiState
import com.example.go2office.presentation.onboarding.OnboardingViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek

@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultWeekdays = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    )

    @Test
    fun GIVEN_onboarding_screen_WHEN_loaded_THEN_should_show_title() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("Setup Go2Office").assertIsDisplayed()
    }

    @Test
    fun GIVEN_step_one_WHEN_loaded_THEN_should_show_step_indicator() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("Step 1 of 5").assertIsDisplayed()
    }

    @Test
    fun GIVEN_step_one_WHEN_loaded_THEN_should_show_days_selection() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("days per week", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_step_two_WHEN_loaded_THEN_should_show_hours_selection() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 1, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("hours", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_next_button_WHEN_displayed_THEN_should_be_visible() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 0, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun GIVEN_step_greater_than_zero_WHEN_loaded_THEN_should_show_back_button() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(OnboardingUiState(currentStep = 1, weekdayPreferences = defaultWeekdays))
        composeTestRule.setContent { OnboardingScreen(viewModel = viewModel, onComplete = {}) }
        composeTestRule.onNode(hasContentDescription("Back")).assertIsDisplayed()
    }
}

