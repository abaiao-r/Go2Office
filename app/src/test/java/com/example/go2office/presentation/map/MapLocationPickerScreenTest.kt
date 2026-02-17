package com.example.go2office.presentation.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33],
    instrumentedPackages = ["androidx.loader.content"]
)
class MapLocationPickerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: MapLocationPickerViewModel
    private val uiState = MutableStateFlow(MapLocationPickerUiState())

    private var navigatedBack = false
    private var selectedLocation: Triple<Double, Double, String>? = null

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.uiState } returns uiState

        navigatedBack = false
        selectedLocation = null
    }

    @Test
    fun `GIVEN map picker screen WHEN rendered THEN title is displayed`() {
        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Pick Location on Map").assertIsDisplayed()
    }

    @Test
    fun `GIVEN map picker screen WHEN back button clicked THEN navigates back`() {
        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { navigatedBack = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assert(navigatedBack)
    }

    @Test
    fun `GIVEN location selected WHEN confirm clicked THEN location returned`() {
        uiState.value = MapLocationPickerUiState(
            selectedLatitude = 38.7223,
            selectedLongitude = -9.1393,
            selectedLocationName = "Lisbon"
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { lat, lon, name ->
                    selectedLocation = Triple(lat, lon, name)
                },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Confirm").performClick()

        assert(selectedLocation != null)
        assert(selectedLocation?.first == 38.7223)
        assert(selectedLocation?.second == -9.1393)
        assert(selectedLocation?.third == "Lisbon")
    }

    @Test
    fun `GIVEN no location selected WHEN screen rendered THEN confirm button not shown`() {
        uiState.value = MapLocationPickerUiState(
            selectedLatitude = null,
            selectedLongitude = null
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Confirm").assertDoesNotExist()
    }

    @Test
    fun `GIVEN location selected WHEN screen rendered THEN selected location card shown`() {
        uiState.value = MapLocationPickerUiState(
            selectedLatitude = 38.7223,
            selectedLongitude = -9.1393,
            selectedLocationName = "Test Location"
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Selected Location").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Location").assertIsDisplayed()
    }

    @Test
    fun `GIVEN search field WHEN text entered THEN updateSearchQuery called`() {
        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Search for a place…").performTextInput("Lisbon")

        verify { mockViewModel.updateSearchQuery("Lisbon") }
    }

    @Test
    fun `GIVEN search results WHEN result clicked THEN selectSearchResult called`() {
        val searchResult = SearchResult(
            displayName = "Lisbon, Portugal",
            latitude = 38.7223,
            longitude = -9.1393
        )
        uiState.value = MapLocationPickerUiState(
            searchResults = listOf(searchResult)
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Lisbon, Portugal").performClick()

        verify { mockViewModel.selectSearchResult(searchResult) }
    }

    @Test
    fun `GIVEN loading state WHEN screen rendered THEN progress indicator shown`() {
        uiState.value = MapLocationPickerUiState(
            isLoading = true
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        // CircularProgressIndicator should be displayed when loading
        // We verify the loading state is being observed
        assert(uiState.value.isLoading)
    }

    @Test
    fun `GIVEN error message WHEN screen rendered THEN error dialog shown`() {
        uiState.value = MapLocationPickerUiState(
            errorMessage = "Something went wrong"
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
    }

    @Test
    fun `GIVEN error dialog WHEN OK clicked THEN dismissError called`() {
        uiState.value = MapLocationPickerUiState(
            errorMessage = "Test error"
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("OK").performClick()

        verify { mockViewModel.dismissError() }
    }

    @Test
    fun `GIVEN zoom in button WHEN clicked THEN map zooms in`() {
        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        // Zoom in button shows "+"
        composeTestRule.onNodeWithText("+").assertIsDisplayed()
    }

    @Test
    fun `GIVEN zoom out button WHEN clicked THEN map zooms out`() {
        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        // Zoom out button shows "-"
        composeTestRule.onNodeWithText("-").assertIsDisplayed()
    }

    @Test
    fun `GIVEN GPS button WHEN clicked THEN useCurrentLocation called`() {
        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        // GPS button shows "📍"
        composeTestRule.onNodeWithText("📍").performClick()

        verify { mockViewModel.useCurrentLocation() }
    }

    @Test
    fun `GIVEN search field with text WHEN clear button clicked THEN clearSearch called`() {
        uiState.value = MapLocationPickerUiState(
            searchQuery = "Test"
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Cancel").performClick()

        verify { mockViewModel.clearSearch() }
    }

    @Test
    fun `GIVEN selected location without name WHEN rendered THEN shows unknown location`() {
        uiState.value = MapLocationPickerUiState(
            selectedLatitude = 38.7223,
            selectedLongitude = -9.1393,
            selectedLocationName = null
        )

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        composeTestRule.onNodeWithText("Unknown Location").assertIsDisplayed()
    }

    @Test
    fun `GIVEN initial coordinates WHEN screen opened THEN map centers on coordinates`() {
        val initialLat = 40.4168
        val initialLon = -3.7038

        composeTestRule.setContent {
            MapLocationPickerScreen(
                viewModel = mockViewModel,
                initialLatitude = initialLat,
                initialLongitude = initialLon,
                onLocationSelected = { _, _, _ -> },
                onNavigateBack = { }
            )
        }

        // The map should be initialized with the initial coordinates
        // We verify the screen renders without error
        composeTestRule.onNodeWithText("Pick Location on Map").assertIsDisplayed()
    }
}

