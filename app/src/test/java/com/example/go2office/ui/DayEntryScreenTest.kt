package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.go2office.presentation.dayentry.DayEntryScreen
import com.example.go2office.presentation.dayentry.DayEntryUiState
import com.example.go2office.presentation.dayentry.DayEntryViewModel
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
class DayEntryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDate = LocalDate.of(2026, 2, 16)

    @Test
    fun `GIVEN entry screen WHEN loaded THEN should render without crash`() {
        val viewModel = mockk<DayEntryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(DayEntryUiState(selectedDate = testDate, isLoading = false))
        composeTestRule.setContent { DayEntryScreen(date = testDate, viewModel = viewModel, onNavigateBack = {}) }
        // Test passes if no crash occurs during rendering
    }

    @Test
    fun `GIVEN back button WHEN clicked THEN should navigate back`() {
        val viewModel = mockk<DayEntryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(DayEntryUiState(selectedDate = testDate, isLoading = false))
        var navigatedBack = false
        composeTestRule.setContent { DayEntryScreen(date = testDate, viewModel = viewModel, onNavigateBack = { navigatedBack = true }) }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack)
    }

    @Test
    fun `GIVEN existing entry WHEN loaded THEN should show delete icon`() {
        val viewModel = mockk<DayEntryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(DayEntryUiState(selectedDate = testDate, isLoading = false, isExistingEntry = true))
        composeTestRule.setContent { DayEntryScreen(date = testDate, viewModel = viewModel, onNavigateBack = {}) }
        composeTestRule.onNode(hasContentDescription("Delete")).assertIsDisplayed()
    }
}
