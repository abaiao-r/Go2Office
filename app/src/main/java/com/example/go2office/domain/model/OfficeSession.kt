package com.example.go2office.domain.model

import java.time.LocalDateTime
import java.time.Duration

data class OfficeSession(
    val id: Long = 0,
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime? = null,
    val isAutoDetected: Boolean = false
) {
    val duration: Duration
        get() = Duration.between(entryTime, exitTime ?: LocalDateTime.now())

    val durationMinutes: Long
        get() = duration.toMinutes()

    val isActive: Boolean
        get() = exitTime == null
}

