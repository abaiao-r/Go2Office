package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.presentation.history.MonthlyHistoryScreen
import com.example.go2office.presentation.history.MonthlyHistoryUiState
import com.example.go2office.presentation.history.MonthlyHistoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class MonthlyHistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testEntries = listOf(
        DailyEntry(date = LocalDate.of(2026, 2, 10), wasInOffice = true, hoursWorked = 8f),
        DailyEntry(date = LocalDate.of(2026, 2, 11), wasInOffice = true, hoursWorked = 7.5f)
    )

    @Test
    fun GIVEN_history_screen_WHEN_loaded_THEN_should_show_title() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(
                selectedMonth = YearMonth.of(2026, 2),
                dailyEntries = testEntries,
                isLoading = false
            )
        )

        composeTestRule.setContent {
            MonthlyHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToDayEntry = {}
            )
        }

        composeTestRule.onNodeWithText("Monthly History").assertIsDisplayed()
    }

    @Test
    fun GIVEN_entries_exist_WHEN_loaded_THEN_should_show_daily_breakdown() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(
                selectedMonth = YearMonth.of(2026, 2),
                dailyEntries = testEntries,
                totalDaysWorked = 2,
                totalHoursWorked = 15.5f,
                isLoading = false
            )
        )

        composeTestRule.setContent {
            MonthlyHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToDayEntry = {}
            )
        }

        composeTestRule.onNodeWithText("Daily Breakdown").assertIsDisplayed()
    }

    @Test
    fun GIVEN_back_button_WHEN_clicked_THEN_should_navigate_back() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(
                selectedMonth = YearMonth.of(2026, 2),
                isLoading = false
            )
        )

        var navigatedBack = false
        composeTestRule.setContent {
            MonthlyHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navigatedBack = true },
                onNavigateToDayEntry = {}
            )
        }

        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack) { "Should have navigated back" }
    }

    @Test
    fun GIVEN_no_entries_WHEN_loaded_THEN_should_show_empty_message() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(
                selectedMonth = YearMonth.of(2026, 2),
                dailyEntries = emptyList(),
                isLoading = false
            )
        )

        composeTestRule.setContent {
            MonthlyHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToDayEntry = {}
            )
        }

        composeTestRule.onNodeWithText("No entries", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun GIVEN_summary_data_WHEN_loaded_THEN_should_show_totals() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(
                selectedMonth = YearMonth.of(2026, 2),
                dailyEntries = testEntries,
                totalDaysWorked = 5,
                totalHoursWorked = 40f,
                requiredDays = 8,
                requiredHours = 64f,
                isLoading = false
            )
        )

        composeTestRule.setContent {
            MonthlyHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToDayEntry = {}
            )
        }

        composeTestRule.onNodeWithText("5", substring = true).assertIsDisplayed()
    }
}

