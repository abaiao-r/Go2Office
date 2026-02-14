package com.example.go2office.domain.model
import java.time.LocalDate
enum class HolidayType {
    PUBLIC_HOLIDAY,  
    VACATION         
}
data class Holiday(
    val id: Long = 0,
    val date: LocalDate,
    val description: String,
    val type: HolidayType = HolidayType.PUBLIC_HOLIDAY
)
