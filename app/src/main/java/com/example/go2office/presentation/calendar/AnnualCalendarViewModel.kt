package com.example.go2office.presentation.calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.go2office.data.remote.HolidayApiService
import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.model.HolidayType
import com.example.go2office.domain.repository.OfficeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
@HiltViewModel
class AnnualCalendarViewModel @Inject constructor(
    private val repository: OfficeRepository,
    private val holidayApiService: HolidayApiService
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnnualCalendarUiState())
    val uiState: StateFlow<AnnualCalendarUiState> = _uiState.asStateFlow()
    init {
        loadHolidays()
        loadAvailableCountries()
    }
    fun changeYear(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
        loadHolidays()
    }
    private fun loadHolidays() {
        viewModelScope.launch {
            val year = _uiState.value.selectedYear
            repository.getHolidaysInRange(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))
                .collect { holidays -> _uiState.update { it.copy(holidays = holidays) } }
        }
    }
    private fun loadAvailableCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCountries = true) }
            val result = holidayApiService.fetchAvailableCountries()
            result.onSuccess { countries ->
                _uiState.update { it.copy(availableCountries = countries, isLoadingCountries = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingCountries = false, error = "Failed to load countries") }
            }
        }
    }
    fun addHoliday(date: LocalDate, description: String, type: HolidayType) {
        viewModelScope.launch {
            repository.saveHoliday(Holiday(date = date, description = description, type = type))
        }
    }
    fun addVacationRange(startDate: LocalDate, endDate: LocalDate, description: String) {
        viewModelScope.launch {
            var currentDate = startDate
            while (!currentDate.isAfter(endDate)) {
                if (currentDate.dayOfWeek.value <= 5) {
                    repository.saveHoliday(Holiday(date = currentDate, description = description, type = HolidayType.VACATION))
                }
                currentDate = currentDate.plusDays(1)
            }
        }
    }
    fun removeHoliday(holidayId: Long) {
        viewModelScope.launch {
            _uiState.value.holidays.find { it.id == holidayId }?.let { repository.deleteHoliday(it.date) }
        }
    }
    fun loadCountryHolidays(countryCode: String, countryName: String, year: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHolidays = true, error = null) }
            val result = holidayApiService.fetchPublicHolidays(countryCode, year)
            result.onSuccess { holidayDtos ->
                holidayDtos.forEach { dto ->
                    val holiday = Holiday(
                        date = LocalDate.parse(dto.date), 
                        description = dto.localName, 
                        type = HolidayType.PUBLIC_HOLIDAY
                    )
                    repository.saveHoliday(holiday)
                }
                _uiState.update {
                    it.copy(
                        selectedCountry = countryName,
                        isLoadingHolidays = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingHolidays = false,
                        error = "Failed to load holidays: ${error.message}"
                    )
                }
            }
        }
    }
    fun unloadCountryHolidays() {
        viewModelScope.launch {
            val year = _uiState.value.selectedYear
            val startDate = LocalDate.of(year, 1, 1)
            val endDate = LocalDate.of(year, 12, 31)
            _uiState.value.holidays
                .filter { it.type == HolidayType.PUBLIC_HOLIDAY }
                .forEach { holiday ->
                    repository.deleteHoliday(holiday.date)
                }
            _uiState.update { it.copy(selectedCountry = "") }
        }
    }
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
data class AnnualCalendarUiState(
    val selectedYear: Int = LocalDate.now().year,
    val holidays: List<Holiday> = emptyList(),
    val selectedCountry: String = "",
    val availableCountries: List<com.example.go2office.data.remote.CountryDto> = emptyList(),
    val isLoadingCountries: Boolean = false,
    val isLoadingHolidays: Boolean = false,
    val error: String? = null
)
