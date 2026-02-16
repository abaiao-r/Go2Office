package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
import com.example.go2office.presentation.calendar.AnnualCalendarScreen
import com.example.go2office.presentation.calendar.AnnualCalendarUiState
import com.example.go2office.presentation.calendar.AnnualCalendarViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AnnualCalendarScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testHolidays = listOf(
        Holiday(id = 1, date = LocalDate.of(2026, 1, 1), description = "New Year", type = HolidayType.PUBLIC_HOLIDAY),
        Holiday(id = 2, date = LocalDate.of(2026, 12, 25), description = "Christmas", type = HolidayType.PUBLIC_HOLIDAY)
    )

    @Test
    fun `GIVEN calendar screen WHEN loaded THEN should show title with year`() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026, holidays = testHolidays))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Holidays & Vacation 2026").assertIsDisplayed()
    }

    @Test
    fun `GIVEN back button WHEN clicked THEN should navigate back`() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026))
        var navigatedBack = false
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = { navigatedBack = true }) }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack)
    }

    @Test
    fun `GIVEN add vacation button WHEN displayed THEN should be visible`() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Add Vacation").assertIsDisplayed()
    }

    @Test
    fun `GIVEN add holiday button WHEN displayed THEN should be visible`() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Add Holiday").assertIsDisplayed()
    }

    @Test
    fun `GIVEN holidays exist WHEN loaded THEN should display holiday`() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026, holidays = testHolidays))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("New Year", substring = true).assertIsDisplayed()
    }
}
