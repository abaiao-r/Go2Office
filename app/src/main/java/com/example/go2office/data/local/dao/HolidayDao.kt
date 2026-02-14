package com.example.go2office.data.local.dao
import androidx.room.*
import com.example.go2office.data.local.entities.HolidayEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
@Dao
interface HolidayDao {
    @Query("SELECT * FROM holidays WHERE date = :date")
    suspend fun getByDate(date: LocalDate): HolidayEntity?
    @Query("SELECT * FROM holidays WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getHolidaysInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<HolidayEntity>>
    @Query("SELECT * FROM holidays WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getHolidaysInRangeOnce(startDate: LocalDate, endDate: LocalDate): List<HolidayEntity>
    @Query("SELECT * FROM holidays ORDER BY date DESC")
    fun getAllHolidays(): Flow<List<HolidayEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(holiday: HolidayEntity): Long
    @Delete
    suspend fun delete(holiday: HolidayEntity)
    @Query("DELETE FROM holidays WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)
    @Query("DELETE FROM holidays")
    suspend fun deleteAll()
}
