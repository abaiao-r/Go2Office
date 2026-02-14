package com.example.go2office.data.local.dao

import androidx.room.*
import com.example.go2office.data.local.entities.MonthlyLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

/**
 * DAO for monthly log operations.
 */
@Dao
interface MonthlyLogDao {

    @Query("SELECT * FROM monthly_logs WHERE yearMonth = :yearMonth")
    suspend fun getByYearMonth(yearMonth: YearMonth): MonthlyLogEntity?

    @Query("SELECT * FROM monthly_logs WHERE yearMonth = :yearMonth")
    fun getByYearMonthFlow(yearMonth: YearMonth): Flow<MonthlyLogEntity?>

    @Query("SELECT * FROM monthly_logs ORDER BY yearMonth DESC")
    fun getAllLogs(): Flow<List<MonthlyLogEntity>>

    @Query("SELECT * FROM monthly_logs ORDER BY yearMonth DESC")
    suspend fun getAllLogsOnce(): List<MonthlyLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: MonthlyLogEntity): Long

    @Update
    suspend fun update(log: MonthlyLogEntity)

    @Delete
    suspend fun delete(log: MonthlyLogEntity)

    @Query("DELETE FROM monthly_logs")
    suspend fun deleteAll()
}

