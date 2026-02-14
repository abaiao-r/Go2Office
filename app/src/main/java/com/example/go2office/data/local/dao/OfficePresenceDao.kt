package com.example.go2office.data.local.dao
import androidx.room.*
import com.example.go2office.data.local.entities.OfficePresenceEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
@Dao
interface OfficePresenceDao {
    @Query("SELECT * FROM office_presence WHERE exitTime IS NULL ORDER BY entryTime DESC LIMIT 1")
    suspend fun getCurrentActiveSession(): OfficePresenceEntity?
    @Query("SELECT * FROM office_presence WHERE exitTime IS NULL")
    fun getActiveSessions(): Flow<List<OfficePresenceEntity>>
    @Query("""
        SELECT * FROM office_presence 
        WHERE date(entryTime) = date(:date) 
        ORDER BY entryTime ASC
    """)
    suspend fun getSessionsForDate(date: String): List<OfficePresenceEntity>
    @Query("""
        SELECT * FROM office_presence 
        WHERE date(entryTime) BETWEEN date(:startDate) AND date(:endDate)
        ORDER BY entryTime DESC
    """)
    fun getSessionsInRange(startDate: String, endDate: String): Flow<List<OfficePresenceEntity>>
    @Query("SELECT * FROM office_presence ORDER BY entryTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 10): Flow<List<OfficePresenceEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: OfficePresenceEntity): Long
    @Update
    suspend fun update(session: OfficePresenceEntity)
    @Delete
    suspend fun delete(session: OfficePresenceEntity)
    @Query("DELETE FROM office_presence WHERE id = :sessionId")
    suspend fun deleteById(sessionId: Long)
    @Query("DELETE FROM office_presence")
    suspend fun deleteAll()
}
