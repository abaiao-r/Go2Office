package com.example.go2office.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.go2office.data.local.dao.DailyEntryDao
import com.example.go2office.data.local.dao.HolidayDao
import com.example.go2office.data.local.dao.MonthlyLogDao
import com.example.go2office.data.local.dao.OfficeLocationDao
import com.example.go2office.data.local.dao.OfficePresenceDao
import com.example.go2office.data.local.dao.OfficeSettingsDao
import com.example.go2office.data.local.entities.Converters
import com.example.go2office.data.local.entities.DailyEntryEntity
import com.example.go2office.data.local.entities.HolidayEntity
import com.example.go2office.data.local.entities.MonthlyLogEntity
import com.example.go2office.data.local.entities.OfficeLocationEntity
import com.example.go2office.data.local.entities.OfficePresenceEntity
import com.example.go2office.data.local.entities.OfficeSettingsEntity

/**
 * Room database for Go2Office app.
 * Contains all entities and DAOs.
 */
@Database(
    entities = [
        OfficeSettingsEntity::class,
        DailyEntryEntity::class,
        MonthlyLogEntity::class,
        HolidayEntity::class,
        OfficeLocationEntity::class,
        OfficePresenceEntity::class
    ],
    version = 3, // Incremented for holiday type field
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class OfficeDatabase : RoomDatabase() {

    abstract fun officeSettingsDao(): OfficeSettingsDao
    abstract fun dailyEntryDao(): DailyEntryDao
    abstract fun monthlyLogDao(): MonthlyLogDao
    abstract fun holidayDao(): HolidayDao
    abstract fun officeLocationDao(): OfficeLocationDao
    abstract fun officePresenceDao(): OfficePresenceDao

    companion object {
        const val DATABASE_NAME = "office_database"
    }
}

