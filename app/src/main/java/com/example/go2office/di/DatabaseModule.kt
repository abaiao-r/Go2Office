package com.example.go2office.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.go2office.data.local.OfficeDatabase
import com.example.go2office.data.local.dao.DailyEntryDao
import com.example.go2office.data.local.dao.HolidayDao
import com.example.go2office.data.local.dao.MonthlyLogDao
import com.example.go2office.data.local.dao.OfficeLocationDao
import com.example.go2office.data.local.dao.OfficePresenceDao
import com.example.go2office.data.local.dao.OfficeSettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database and DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add type column to holidays table with default value PUBLIC_HOLIDAY
            database.execSQL(
                "ALTER TABLE holidays ADD COLUMN type TEXT NOT NULL DEFAULT 'PUBLIC_HOLIDAY'"
            )
        }
    }

    @Provides
    @Singleton
    fun provideOfficeDatabase(@ApplicationContext context: Context): OfficeDatabase {
        return Room.databaseBuilder(
            context,
            OfficeDatabase::class.java,
            "office_database"
        )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideOfficeSettingsDao(database: OfficeDatabase): OfficeSettingsDao {
        return database.officeSettingsDao()
    }

    @Provides
    fun provideDailyEntryDao(database: OfficeDatabase): DailyEntryDao {
        return database.dailyEntryDao()
    }

    @Provides
    fun provideMonthlyLogDao(database: OfficeDatabase): MonthlyLogDao {
        return database.monthlyLogDao()
    }

    @Provides
    fun provideHolidayDao(database: OfficeDatabase): HolidayDao {
        return database.holidayDao()
    }

    @Provides
    fun provideOfficeLocationDao(database: OfficeDatabase): OfficeLocationDao {
        return database.officeLocationDao()
    }

    @Provides
    fun provideOfficePresenceDao(database: OfficeDatabase): OfficePresenceDao {
        return database.officePresenceDao()
    }
}

