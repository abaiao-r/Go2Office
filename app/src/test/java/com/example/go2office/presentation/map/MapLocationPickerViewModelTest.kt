package com.example.go2office.presentation.map

import android.content.Context
import app.cash.turbine.test
import com.google.android.gms.location.FusedLocationProviderClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapLocationPickerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var context: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: MapLocationPickerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
        fusedLocationClient = mockk(relaxed = true)

        every { context.packageName } returns "com.example.go2office"

        mockkStatic("com.google.android.gms.location.LocationServices")
        every {
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(any<Context>())
        } returns fusedLocationClient

        viewModel = MapLocationPickerViewModel(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN initial state WHEN viewModel created THEN state has no selected location`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedLatitude)
            assertNull(state.selectedLongitude)
            assertNull(state.selectedLocationName)
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResults.isEmpty())
            assertFalse(state.isSearching)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `GIVEN no location selected WHEN setSelectedLocation called THEN state updates with coordinates`() = runTest {
        val latitude = 38.7223
        val longitude = -9.1393

        viewModel.setSelectedLocation(latitude, longitude)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(latitude, state.selectedLatitude)
            assertEquals(longitude, state.selectedLongitude)
        }
    }

    @Test
    fun `GIVEN empty search query WHEN updateSearchQuery called THEN state updates query`() = runTest {
        val query = "Lisbon"

        viewModel.updateSearchQuery(query)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(query, state.searchQuery)
            assertTrue(state.searchResults.isEmpty())
        }
    }

    @Test
    fun `GIVEN search query exists WHEN clearSearch called THEN query and results cleared`() = runTest {
        viewModel.updateSearchQuery("Lisbon")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearSearch()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResults.isEmpty())
        }
    }

    @Test
    fun `GIVEN empty search query WHEN searchLocation called THEN nothing happens`() = runTest {
        viewModel.updateSearchQuery("")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.searchLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSearching)
            assertTrue(state.searchResults.isEmpty())
        }
    }

    @Test
    fun `GIVEN search result WHEN selectSearchResult called THEN location and name updated`() = runTest {
        val searchResult = SearchResult(
            displayName = "Praça do Comércio, Lisbon, Portugal",
            latitude = 38.7077,
            longitude = -9.1365
        )

        viewModel.selectSearchResult(searchResult)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(searchResult.latitude, state.selectedLatitude)
            assertEquals(searchResult.longitude, state.selectedLongitude)
            assertEquals("Praça do Comércio", state.selectedLocationName)
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResults.isEmpty())
        }
    }

    @Test
    fun `GIVEN error message displayed WHEN dismissError called THEN error cleared`() = runTest {
        viewModel.dismissError()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `GIVEN location selected WHEN setSelectedLocation called again THEN location name reset`() = runTest {
        viewModel.setSelectedLocation(38.7223, -9.1393)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setSelectedLocation(40.4168, -3.7038)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(40.4168, state.selectedLatitude)
            assertEquals(-3.7038, state.selectedLongitude)
            assertNull(state.selectedLocationName)
        }
    }

    @Test
    fun `GIVEN search result with comma separated name WHEN selectSearchResult called THEN first part used as name`() = runTest {
        val searchResult = SearchResult(
            displayName = "Torre de Belém, Av. Brasília, Belém, Lisbon, Portugal",
            latitude = 38.6916,
            longitude = -9.2160
        )

        viewModel.selectSearchResult(searchResult)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Torre de Belém", state.selectedLocationName)
        }
    }

    @Test
    fun `GIVEN search query with whitespace WHEN searchLocation called THEN query trimmed`() = runTest {
        viewModel.updateSearchQuery("   ")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.searchLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSearching)
        }
    }
}

