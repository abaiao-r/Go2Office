package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.go2office.domain.model.MonthProgress
import com.example.go2office.domain.model.SuggestedDay
import com.example.go2office.presentation.dashboard.DashboardScreen
import com.example.go2office.presentation.dashboard.DashboardUiState
import com.example.go2office.presentation.dashboard.DashboardViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testProgress = MonthProgress(
        yearMonth = YearMonth.of(2026, 2),
        requiredDays = 8,
        completedDays = 3,
        requiredHours = 64f,
        completedHours = 24f
    )

    private val testSuggestedDays = listOf(
        SuggestedDay(
            date = LocalDate.of(2026, 2, 16),
            dayOfWeek = DayOfWeek.MONDAY,
            reason = "Preferred day",
            priority = 1
        )
    )

    @Test
    fun GIVEN_progress_data_WHEN_screen_renders_THEN_should_show_title() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        val uiState = MutableStateFlow(
            DashboardUiState(
                isLoading = false,
                monthProgress = testProgress,
                suggestedDays = testSuggestedDays
            )
        )
        every { viewModel.uiState } returns uiState

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToDayEntry = {},
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNodeWithText("Go2Office").assertIsDisplayed()
    }

    @Test
    fun GIVEN_user_at_office_WHEN_screen_renders_THEN_should_show_at_office_status() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        val uiState = MutableStateFlow(
            DashboardUiState(
                isLoading = false,
                monthProgress = testProgress,
                isAtOffice = true,
                todayTotalHours = 2.5f
            )
        )
        every { viewModel.uiState } returns uiState

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToDayEntry = {},
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNodeWithText("At the Office", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_user_not_at_office_WHEN_screen_renders_THEN_should_show_not_at_office_status() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        val uiState = MutableStateFlow(
            DashboardUiState(
                isLoading = false,
                monthProgress = testProgress,
                isAtOffice = false
            )
        )
        every { viewModel.uiState } returns uiState

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToDayEntry = {},
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNodeWithText("Not at Office", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_settings_button_WHEN_clicked_THEN_should_navigate_to_settings() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        val uiState = MutableStateFlow(
            DashboardUiState(
                isLoading = false,
                monthProgress = testProgress
            )
        )
        every { viewModel.uiState } returns uiState

        var navigatedToSettings = false

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToDayEntry = {},
                onNavigateToSettings = { navigatedToSettings = true }
            )
        }

        composeTestRule.onNode(hasContentDescription("Settings")).performClick()

        assert(navigatedToSettings) { "Should have navigated to settings" }
    }

    @Test
    fun GIVEN_FAB_WHEN_clicked_THEN_should_navigate_to_day_entry() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        val uiState = MutableStateFlow(
            DashboardUiState(
                isLoading = false,
                monthProgress = testProgress
            )
        )
        every { viewModel.uiState } returns uiState

        var navigatedDate: LocalDate? = null

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToDayEntry = { navigatedDate = it },
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNode(hasContentDescription("Add entry")).performClick()

        assert(navigatedDate != null) { "Should have navigated to day entry" }
    }
}

