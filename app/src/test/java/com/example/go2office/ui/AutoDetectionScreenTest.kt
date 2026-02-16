package com.example.go2office.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.go2office.domain.model.OfficeLocation
import com.example.go2office.presentation.autodetection.AutoDetectionScreen
import com.example.go2office.presentation.autodetection.AutoDetectionUiState
import com.example.go2office.presentation.autodetection.AutoDetectionViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AutoDetectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testLocation = OfficeLocation(
        latitude = 38.7223,
        longitude = -9.1393,
        radiusMeters = 100f,
        name = "Main Office"
    )

    @Test
    fun `GIVEN auto detection screen WHEN loaded THEN should render without crash`() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState(isLoading = false))
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = {}) }
        // Test passes if no crash occurs during rendering
    }

    @Test
    fun `GIVEN back button WHEN clicked THEN should navigate back`() {
        val viewModel = mockk<AutoDetectionViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(AutoDetectionUiState(isLoading = false))
        var navigatedBack = false
        composeTestRule.setContent { AutoDetectionScreen(viewModel = viewModel, onNavigateBack = { navigatedBack = true }) }
        composeTestRule.onNode(hasContentDescription("Back")).performClick()
        assert(navigatedBack)
    }
}
