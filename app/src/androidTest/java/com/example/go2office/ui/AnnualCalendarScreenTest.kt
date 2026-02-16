package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AnnualCalendarScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testHolidays = listOf(
        Holiday(id = 1, date = LocalDate.of(2026, 1, 1), description = "New Year", type = HolidayType.PUBLIC_HOLIDAY),
        Holiday(id = 2, date = LocalDate.of(2026, 12, 25), description = "Christmas", type = HolidayType.PUBLIC_HOLIDAY)
    )

    @Test
    fun GIVEN_calendar_screen_WHEN_loaded_THEN_should_show_title_with_year() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026, holidays = testHolidays))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Holidays & Vacation 2026").assertIsDisplayed()
    }

    @Test
    fun GIVEN_back_button_WHEN_clicked_THEN_should_navigate_back() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026))
        var navigatedBack = false
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = { navigatedBack = true }) }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack)
    }

    @Test
    fun GIVEN_add_vacation_button_WHEN_displayed_THEN_should_be_visible() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Add Vacation").assertIsDisplayed()
    }

    @Test
    fun GIVEN_add_holiday_button_WHEN_displayed_THEN_should_be_visible() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Add Holiday").assertIsDisplayed()
    }

    @Test
    fun GIVEN_holidays_exist_WHEN_loaded_THEN_should_display_holiday() {
        val viewModel = mockk<AnnualCalendarViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AnnualCalendarUiState(selectedYear = 2026, holidays = testHolidays))
        composeTestRule.setContent { AnnualCalendarScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("New Year", substring = true).assertIsDisplayed()
    }
}

