package com.example.go2office.data.local.entities

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

/**
 * Type converters for Room database.
 * Handles conversion of complex types to/from database primitives.
 */
class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    // LocalDate converters
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    // YearMonth converters
    @TypeConverter
    fun fromYearMonth(yearMonth: YearMonth?): String? {
        return yearMonth?.toString()
    }

    @TypeConverter
    fun toYearMonth(yearMonthString: String?): YearMonth? {
        return yearMonthString?.let { YearMonth.parse(it) }
    }

    // Instant converters
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(epochMilli: Long?): Instant? {
        return epochMilli?.let { Instant.ofEpochMilli(it) }
    }

    // List<DayOfWeek> converters (for weekday preferences)
    @TypeConverter
    fun fromDayOfWeekList(dayList: List<DayOfWeek>?): String? {
        return dayList?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayOfWeekList(dayString: String?): List<DayOfWeek>? {
        return dayString?.split(",")?.map { DayOfWeek.valueOf(it) }
    }

    // LocalDateTime converters
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it) }
    }
}

