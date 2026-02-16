package com.example.go2office.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.go2office.R
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context get() = ApplicationProvider.getApplicationContext()

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
    fun `GIVEN progress data WHEN screen renders THEN should show title`() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            DashboardUiState(isLoading = false, monthProgress = testProgress, suggestedDays = testSuggestedDays)
        )
        composeTestRule.setContent {
            DashboardScreen(viewModel = viewModel, onNavigateToDayEntry = {}, onNavigateToSettings = {})
        }
        composeTestRule.onNodeWithText(context.getString(R.string.dashboard_title)).assertIsDisplayed()
    }

    @Test
    fun `GIVEN user at office WHEN screen renders THEN should show at office status`() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            DashboardUiState(isLoading = false, monthProgress = testProgress, isAtOffice = true, todayTotalHours = 2.5f)
        )
        composeTestRule.setContent {
            DashboardScreen(viewModel = viewModel, onNavigateToDayEntry = {}, onNavigateToSettings = {})
        }
        composeTestRule.onNodeWithText("At Office", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun `GIVEN user not at office WHEN screen renders THEN should show not at office status`() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            DashboardUiState(isLoading = false, monthProgress = testProgress, isAtOffice = false)
        )
        composeTestRule.setContent {
            DashboardScreen(viewModel = viewModel, onNavigateToDayEntry = {}, onNavigateToSettings = {})
        }
        composeTestRule.onNodeWithText("Not at Office", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun `GIVEN settings button WHEN clicked THEN should navigate to settings`() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            DashboardUiState(isLoading = false, monthProgress = testProgress)
        )
        var navigatedToSettings = false
        composeTestRule.setContent {
            DashboardScreen(viewModel = viewModel, onNavigateToDayEntry = {}, onNavigateToSettings = { navigatedToSettings = true })
        }
        composeTestRule.onNode(hasContentDescription(context.getString(R.string.settings_title))).performClick()
        assert(navigatedToSettings) { "Should have navigated to settings" }
    }

    @Test
    fun `GIVEN FAB WHEN clicked THEN should navigate to day entry`() {
        val viewModel = mockk<DashboardViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            DashboardUiState(isLoading = false, monthProgress = testProgress)
        )
        var navigatedDate: LocalDate? = null
        composeTestRule.setContent {
            DashboardScreen(viewModel = viewModel, onNavigateToDayEntry = { navigatedDate = it }, onNavigateToSettings = {})
        }
        composeTestRule.onNode(hasContentDescription(context.getString(R.string.add_entry))).performClick()
        assert(navigatedDate != null) { "Should have navigated to day entry" }
    }
}
