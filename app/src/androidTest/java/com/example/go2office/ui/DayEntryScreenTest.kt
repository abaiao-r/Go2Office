package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.go2office.presentation.dayentry.DayEntryScreen
import com.example.go2office.presentation.dayentry.DayEntryUiState
import com.example.go2office.presentation.dayentry.DayEntryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DayEntryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDate = LocalDate.of(2026, 2, 16)

    @Test
    fun GIVEN_entry_screen_WHEN_loaded_THEN_should_show_title() {
        val viewModel = mockk<DayEntryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(DayEntryUiState(selectedDate = testDate, isLoading = false))
        composeTestRule.setContent { DayEntryScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Day Entry").assertIsDisplayed()
    }

    @Test
    fun GIVEN_back_button_WHEN_clicked_THEN_should_navigate_back() {
        val viewModel = mockk<DayEntryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(DayEntryUiState(selectedDate = testDate, isLoading = false))
        var navigatedBack = false
        composeTestRule.setContent { DayEntryScreen(viewModel = viewModel, onNavigateBack = { navigatedBack = true }) }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack)
    }

    @Test
    fun GIVEN_save_button_WHEN_visible_THEN_should_be_displayed() {
        val viewModel = mockk<DayEntryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(DayEntryUiState(selectedDate = testDate, isLoading = false, wasInOffice = true))
        composeTestRule.setContent { DayEntryScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun GIVEN_existing_entry_WHEN_loaded_THEN_should_show_delete_button() {
        val viewModel = mockk<DayEntryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(DayEntryUiState(selectedDate = testDate, isLoading = false, isExistingEntry = true))
        composeTestRule.setContent { DayEntryScreen(viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
    }
}

