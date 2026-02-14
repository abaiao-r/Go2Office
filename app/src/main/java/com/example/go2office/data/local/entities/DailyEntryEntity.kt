package com.example.go2office.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.Instant
import java.time.LocalDate
@Entity(
    tableName = "daily_entries",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val wasInOffice: Boolean,
    val hoursWorked: Float,
    val notes: String? = null,
    val createdAt: Instant
)
