package com.example.go2office.util

import java.time.ZoneId

/**
 * Utility functions for timezone handling.
 */
object TimeZoneUtils {

    /**
     * Get the system default timezone.
     */
    fun getSystemTimeZone(): ZoneId {
        return ZoneId.systemDefault()
    }

    /**
     * Get the timezone ID as a string.
     */
    fun getTimeZoneId(zoneId: ZoneId = getSystemTimeZone()): String {
        return zoneId.id
    }

    /**
     * Parse a timezone from a string ID.
     */
    fun parseTimeZone(zoneIdString: String): ZoneId? {
        return try {
            ZoneId.of(zoneIdString)
        } catch (e: Exception) {
            null
        }
    }
}

