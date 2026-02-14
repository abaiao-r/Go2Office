package com.example.go2office.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.Instant
import java.time.LocalDate
@Entity(
    tableName = "holidays",
    indices = [Index(value = ["date"], unique = true)]
)
data class HolidayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val description: String,
    val type: String = "PUBLIC_HOLIDAY", 
    val createdAt: Instant
)
