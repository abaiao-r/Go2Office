package com.example.go2office.domain.model

import java.time.LocalDate

/**
 * Type of holiday/non-working day.
 */
enum class HolidayType {
    PUBLIC_HOLIDAY,  // Feriado público (ex: Natal, Ano Novo)
    VACATION         // Férias do usuário
}

/**
 * Domain model representing a holiday or office-not-required day.
 * Both public holidays and personal vacation days exclude from required work days.
 */
data class Holiday(
    val id: Long = 0,
    val date: LocalDate,
    val description: String,
    val type: HolidayType = HolidayType.PUBLIC_HOLIDAY
)

