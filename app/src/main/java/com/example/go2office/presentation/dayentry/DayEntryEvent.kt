package com.example.go2office.presentation.dayentry

import java.time.LocalDate

/**
 * Events for day entry screen.
 */
sealed class DayEntryEvent {
    data class SelectDate(val date: LocalDate) : DayEntryEvent()
    data class ToggleWasInOffice(val value: Boolean) : DayEntryEvent()
    data class UpdateHours(val hours: Float) : DayEntryEvent()
    data class UpdateNotes(val notes: String) : DayEntryEvent()
    object Save : DayEntryEvent()
    object Delete : DayEntryEvent()
    object DismissError : DayEntryEvent()
}

