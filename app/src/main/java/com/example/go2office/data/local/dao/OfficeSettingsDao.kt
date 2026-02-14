package com.example.go2office.data.local.dao
import androidx.room.*
import com.example.go2office.data.local.entities.OfficeSettingsEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface OfficeSettingsDao {
    @Query("SELECT * FROM office_settings WHERE id = 1")
    fun getSettings(): Flow<OfficeSettingsEntity?>
    @Query("SELECT * FROM office_settings WHERE id = 1")
    suspend fun getSettingsOnce(): OfficeSettingsEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: OfficeSettingsEntity)
    @Query("DELETE FROM office_settings")
    suspend fun deleteAll()
}
