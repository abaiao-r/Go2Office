package com.example.go2office.util

/**
 * App-wide constants.
 */
object Constants {

    // Settings constraints
    const val MIN_REQUIRED_DAYS_PER_WEEK = 1
    const val MAX_REQUIRED_DAYS_PER_WEEK = 5
    const val MIN_REQUIRED_HOURS_PER_WEEK = 1f
    const val MAX_REQUIRED_HOURS_PER_WEEK = 40f

    // Daily entry constraints
    const val MIN_HOURS_PER_DAY = 0f
    const val MAX_HOURS_PER_DAY = 24f

    // Auto-detection work hours (7 AM - 7 PM)
    const val WORK_START_HOUR = 7  // 7:00 AM
    const val WORK_END_HOUR = 19   // 7:00 PM (19:00 in 24-hour format)
    const val MAX_DAILY_HOURS = 10f  // Maximum hours counted per day

    // Auto-detection settings
    const val DEFAULT_GEOFENCE_RADIUS_METERS = 100f
    const val MIN_GEOFENCE_RADIUS_METERS = 50f
    const val MAX_GEOFENCE_RADIUS_METERS = 500f
    const val DEFAULT_MINIMUM_VISIT_MINUTES = 15

    // UI defaults
    const val DEFAULT_RECENT_ENTRIES_LIMIT = 10
    const val DEFAULT_SUGGESTED_DAYS_COUNT = 5

    // Database
    const val DATABASE_NAME = "office_database"
}

