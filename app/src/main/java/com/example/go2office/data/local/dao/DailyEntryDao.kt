package com.example.go2office.data.local.dao

import androidx.room.*
import com.example.go2office.data.local.entities.DailyEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * DAO for daily entry operations.
 */
@Dao
interface DailyEntryDao {

    @Query("SELECT * FROM daily_entries WHERE date = :date")
    suspend fun getByDate(date: LocalDate): DailyEntryEntity?

    @Query("SELECT * FROM daily_entries WHERE date = :date")
    fun getByDateFlow(date: LocalDate): Flow<DailyEntryEntity?>

    @Query("SELECT * FROM daily_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyEntryEntity>>

    @Query("SELECT * FROM daily_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getEntriesInRangeOnce(startDate: LocalDate, endDate: LocalDate): List<DailyEntryEntity>

    @Query("SELECT * FROM daily_entries WHERE wasInOffice = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getOfficeDaysInRange(startDate: LocalDate, endDate: LocalDate): List<DailyEntryEntity>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC LIMIT :limit")
    fun getRecentEntries(limit: Int = 10): Flow<List<DailyEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DailyEntryEntity): Long

    @Update
    suspend fun update(entry: DailyEntryEntity)

    @Delete
    suspend fun delete(entry: DailyEntryEntity)

    @Query("DELETE FROM daily_entries WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)

    @Query("DELETE FROM daily_entries")
    suspend fun deleteAll()
}

