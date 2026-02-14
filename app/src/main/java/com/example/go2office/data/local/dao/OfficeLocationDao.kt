package com.example.go2office.data.local.dao

import androidx.room.*
import com.example.go2office.data.local.entities.OfficeLocationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for office location operations.
 */
@Dao
interface OfficeLocationDao {

    @Query("SELECT * FROM office_locations WHERE isActive = 1")
    fun getActiveLocations(): Flow<List<OfficeLocationEntity>>

    @Query("SELECT * FROM office_locations WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveLocation(): OfficeLocationEntity?

    @Query("SELECT * FROM office_locations WHERE id = :id")
    suspend fun getById(id: Long): OfficeLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: OfficeLocationEntity): Long

    @Update
    suspend fun update(location: OfficeLocationEntity)

    @Delete
    suspend fun delete(location: OfficeLocationEntity)

    @Query("UPDATE office_locations SET isActive = 0")
    suspend fun deactivateAll()

    @Query("DELETE FROM office_locations")
    suspend fun deleteAll()
}

