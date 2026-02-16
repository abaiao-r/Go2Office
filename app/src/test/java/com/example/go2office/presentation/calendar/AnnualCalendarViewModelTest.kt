package com.example.go2office.presentation.calendar

import com.example.go2office.data.remote.CountryDto
import com.example.go2office.data.remote.HolidayApiService
import com.example.go2office.data.remote.HolidayDto
import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
import com.example.go2office.domain.repository.OfficeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AnnualCalendarViewModelTest {

    private lateinit var repository: OfficeRepository
    private lateinit var holidayApiService: HolidayApiService
    private lateinit var viewModel: AnnualCalendarViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testHolidays = listOf(
        Holiday(id = 1, date = LocalDate.of(2026, 1, 1), description = "New Year", type = HolidayType.PUBLIC_HOLIDAY),
        Holiday(id = 2, date = LocalDate.of(2026, 12, 25), description = "Christmas", type = HolidayType.PUBLIC_HOLIDAY),
        Holiday(id = 3, date = LocalDate.of(2026, 8, 15), description = "Summer Vacation", type = HolidayType.VACATION)
    )

    private val testCountries = listOf(
        CountryDto(countryCode = "PT", name = "Portugal"),
        CountryDto(countryCode = "ES", name = "Spain")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        holidayApiService = mockk()

        every { repository.getHolidaysInRange(any(), any()) } returns flowOf(testHolidays)
        coEvery { holidayApiService.fetchAvailableCountries() } returns Result.success(testCountries)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = AnnualCalendarViewModel(repository, holidayApiService)
    }

    @Test
    fun `GIVEN holidays exist WHEN viewModel initializes THEN should load holidays`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testHolidays, viewModel.uiState.value.holidays)
    }

    @Test
    fun `GIVEN countries available WHEN viewModel initializes THEN should load countries`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testCountries, viewModel.uiState.value.availableCountries)
        assertFalse(viewModel.uiState.value.isLoadingCountries)
    }

    @Test
    fun `GIVEN countries fetch fails WHEN viewModel initializes THEN should show error`() = runTest {
        coEvery { holidayApiService.fetchAvailableCountries() } returns Result.failure(Exception("Network error"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Failed to load countries", viewModel.uiState.value.error)
    }

    @Test
    fun `GIVEN new year WHEN changeYear called THEN should update year and reload holidays`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.changeYear(2027)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2027, viewModel.uiState.value.selectedYear)
    }

    @Test
    fun `GIVEN holiday data WHEN addHoliday called THEN should save to repository`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val date = LocalDate.of(2026, 4, 25)
        viewModel.addHoliday(date, "Freedom Day", HolidayType.PUBLIC_HOLIDAY)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveHoliday(match { it.date == date && it.description == "Freedom Day" }) }
    }

    @Test
    fun `GIVEN vacation range WHEN addVacationRange called THEN should save weekdays only`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val startDate = LocalDate.of(2026, 8, 10) // Monday
        val endDate = LocalDate.of(2026, 8, 14) // Friday
        viewModel.addVacationRange(startDate, endDate, "Summer vacation")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 5) { repository.saveHoliday(match { it.type == HolidayType.VACATION }) }
    }

    @Test
    fun `GIVEN existing holiday WHEN removeHoliday called THEN should delete from repository`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.removeHoliday(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteHoliday(LocalDate.of(2026, 1, 1)) }
    }

    @Test
    fun `GIVEN country selected WHEN loadCountryHolidays called THEN should fetch and save holidays`() = runTest {
        val publicHolidays = listOf(
            HolidayDto(
                date = "2026-04-25",
                localName = "Freedom Day",
                name = "Freedom Day",
                countryCode = "PT",
                global = true,
                counties = emptyList()
            )
        )
        coEvery { holidayApiService.fetchPublicHolidays("PT", 2026) } returns Result.success(publicHolidays)

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadCountryHolidays("PT", "Portugal", 2026)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveHoliday(match { it.date == LocalDate.of(2026, 4, 25) }) }
        assertEquals("Portugal", viewModel.uiState.value.selectedCountry)
        assertFalse(viewModel.uiState.value.isLoadingHolidays)
    }

    @Test
    fun `GIVEN fetch fails WHEN loadCountryHolidays called THEN should show error`() = runTest {
        coEvery { holidayApiService.fetchPublicHolidays(any(), any()) } returns Result.failure(Exception("API error"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadCountryHolidays("PT", "Portugal", 2026)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.error!!.contains("Failed to load holidays"))
    }

    @Test
    fun `GIVEN public holidays loaded WHEN unloadCountryHolidays called THEN should delete public holidays`() = runTest {
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.unloadCountryHolidays()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteHoliday(LocalDate.of(2026, 1, 1)) }
        coVerify { repository.deleteHoliday(LocalDate.of(2026, 12, 25)) }
        assertEquals("", viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun `GIVEN error showing WHEN clearError called THEN should clear error`() = runTest {
        coEvery { holidayApiService.fetchAvailableCountries() } returns Result.failure(Exception("Error"))

        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }
}
