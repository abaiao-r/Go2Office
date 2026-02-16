package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.YearMonth

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MonthlyHistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testEntries = listOf(
        DailyEntry(date = LocalDate.of(2026, 2, 10), wasInOffice = true, hoursWorked = 8f),
        DailyEntry(date = LocalDate.of(2026, 2, 11), wasInOffice = true, hoursWorked = 7.5f)
    )

    @Test
    fun `GIVEN history screen WHEN loaded THEN should render without crash`() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(selectedMonth = YearMonth.of(2026, 2), dailyEntries = testEntries, isLoading = false)
        )
        composeTestRule.setContent {
            MonthlyHistoryScreen(viewModel = viewModel, onNavigateBack = {}, onNavigateToDayEntry = {})
        }
        // Test passes if no crash occurs during rendering
    }

    @Test
    fun `GIVEN history screen WHEN loaded THEN should show title`() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(selectedMonth = YearMonth.of(2026, 2), dailyEntries = testEntries, isLoading = false)
        )
        composeTestRule.setContent {
            MonthlyHistoryScreen(viewModel = viewModel, onNavigateBack = {}, onNavigateToDayEntry = {})
        }
        composeTestRule.onNodeWithText("Monthly History").assertIsDisplayed()
    }

    @Test
    fun `GIVEN back button WHEN clicked THEN should navigate back`() {
        val viewModel = mockk<MonthlyHistoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            MonthlyHistoryUiState(selectedMonth = YearMonth.of(2026, 2), isLoading = false)
        )
        var navigatedBack = false
        composeTestRule.setContent {
            MonthlyHistoryScreen(viewModel = viewModel, onNavigateBack = { navigatedBack = true }, onNavigateToDayEntry = {})
        }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack) { "Should have navigated back" }
    }
}
