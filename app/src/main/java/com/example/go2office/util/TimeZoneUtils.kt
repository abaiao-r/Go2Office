package com.example.go2office.util
import java.time.ZoneId
object TimeZoneUtils {
    fun getSystemTimeZone(): ZoneId {
        return ZoneId.systemDefault()
    }
    fun getTimeZoneId(zoneId: ZoneId = getSystemTimeZone()): String {
        return zoneId.id
    }
    fun parseTimeZone(zoneIdString: String): ZoneId? {
        return try {
            ZoneId.of(zoneIdString)
        } catch (e: Exception) {
            null
        }
    }
}
