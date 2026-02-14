package com.example.go2office.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.Instant
import java.time.YearMonth
@Entity(
    tableName = "monthly_logs",
    indices = [Index(value = ["yearMonth"], unique = true)]
)
data class MonthlyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val yearMonth: YearMonth,
    val requiredDays: Int,
    val requiredHours: Float,
    val completedDays: Int,
    val completedHours: Float,
    val createdAt: Instant
)
