package com.example.go2office.domain.model
import java.time.DayOfWeek
import java.time.LocalDate
data class SuggestedDay(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val reason: String,
    val priority: Int 
)
