package com.example.go2office.data.repository
import com.example.go2office.data.local.dao.DailyEntryDao
import com.example.go2office.data.local.dao.HolidayDao
import com.example.go2office.data.local.dao.MonthlyLogDao
import com.example.go2office.data.local.dao.OfficePresenceDao
import com.example.go2office.data.local.dao.OfficeSettingsDao
import com.example.go2office.data.mapper.DailyEntryMapper
import com.example.go2office.data.mapper.HolidayMapper
import com.example.go2office.data.mapper.MonthlyLogMapper
import com.example.go2office.data.mapper.OfficePresenceMapper
import com.example.go2office.data.mapper.SettingsMapper
import com.example.go2office.domain.model.*
import com.example.go2office.domain.repository.OfficeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class OfficeRepositoryImpl @Inject constructor(
    private val settingsDao: OfficeSettingsDao,
    private val dailyEntryDao: DailyEntryDao,
    private val monthlyLogDao: MonthlyLogDao,
    private val holidayDao: HolidayDao,
    private val officePresenceDao: OfficePresenceDao
) : OfficeRepository {
    override fun getSettings(): Flow<OfficeSettings?> {
        return settingsDao.getSettings().map { entity ->
            entity?.let { SettingsMapper.toDomain(it) }
        }
    }
    override suspend fun getSettingsOnce(): OfficeSettings? {
        return settingsDao.getSettingsOnce()?.let { SettingsMapper.toDomain(it) }
    }
    override suspend fun saveSettings(settings: OfficeSettings): Result<Unit> {
        return try {
            val existingEntity = settingsDao.getSettingsOnce()
            val entity = if (existingEntity != null) {
                SettingsMapper.toEntityUpdate(settings, existingEntity)
            } else {
                SettingsMapper.toEntity(settings)
            }
            settingsDao.insertOrUpdate(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getDailyEntry(date: LocalDate): DailyEntry? {
        return dailyEntryDao.getByDate(date)?.let { DailyEntryMapper.toDomain(it) }
    }
    override fun getDailyEntryFlow(date: LocalDate): Flow<DailyEntry?> {
        return dailyEntryDao.getByDateFlow(date).map { entity ->
            entity?.let { DailyEntryMapper.toDomain(it) }
        }
    }
    override fun getDailyEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyEntry>> {
        return dailyEntryDao.getEntriesInRange(startDate, endDate).map { entities ->
            DailyEntryMapper.toDomainList(entities)
        }
    }
    override suspend fun getDailyEntriesInRangeOnce(startDate: LocalDate, endDate: LocalDate): List<DailyEntry> {
        return DailyEntryMapper.toDomainList(
            dailyEntryDao.getEntriesInRangeOnce(startDate, endDate)
        )
    }
    override suspend fun getOfficeDaysInRange(startDate: LocalDate, endDate: LocalDate): List<DailyEntry> {
        return DailyEntryMapper.toDomainList(
            dailyEntryDao.getOfficeDaysInRange(startDate, endDate)
        )
    }
    override fun getRecentEntries(limit: Int): Flow<List<DailyEntry>> {
        return dailyEntryDao.getRecentEntries(limit).map { entities ->
            DailyEntryMapper.toDomainList(entities)
        }
    }
    override suspend fun saveDailyEntry(entry: DailyEntry): Result<Unit> {
        return try {
            val entity = DailyEntryMapper.toEntity(entry)
            dailyEntryDao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteDailyEntry(date: LocalDate): Result<Unit> {
        return try {
            dailyEntryDao.deleteByDate(date)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getMonthlyLog(yearMonth: YearMonth): MonthlyLog? {
        return monthlyLogDao.getByYearMonth(yearMonth)?.let { MonthlyLogMapper.toDomain(it) }
    }
    override fun getMonthlyLogFlow(yearMonth: YearMonth): Flow<MonthlyLog?> {
        return monthlyLogDao.getByYearMonthFlow(yearMonth).map { entity ->
            entity?.let { MonthlyLogMapper.toDomain(it) }
        }
    }
    override fun getAllMonthlyLogs(): Flow<List<MonthlyLog>> {
        return monthlyLogDao.getAllLogs().map { entities ->
            MonthlyLogMapper.toDomainList(entities)
        }
    }
    override suspend fun saveMonthlyLog(log: MonthlyLog): Result<Unit> {
        return try {
            val entity = MonthlyLogMapper.toEntity(log)
            monthlyLogDao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getHoliday(date: LocalDate): Holiday? {
        return holidayDao.getByDate(date)?.let { HolidayMapper.toDomain(it) }
    }
    override fun getHolidaysInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Holiday>> {
        return holidayDao.getHolidaysInRange(startDate, endDate).map { entities ->
            HolidayMapper.toDomainList(entities)
        }
    }
    override suspend fun getHolidaysInRangeOnce(startDate: LocalDate, endDate: LocalDate): List<Holiday> {
        return HolidayMapper.toDomainList(
            holidayDao.getHolidaysInRangeOnce(startDate, endDate)
        )
    }
    override fun getAllHolidays(): Flow<List<Holiday>> {
        return holidayDao.getAllHolidays().map { entities ->
            HolidayMapper.toDomainList(entities)
        }
    }
    override suspend fun saveHoliday(holiday: Holiday): Result<Unit> {
        return try {
            val entity = HolidayMapper.toEntity(holiday)
            holidayDao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteHoliday(date: LocalDate): Result<Unit> {
        return try {
            holidayDao.deleteByDate(date)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun getActiveOfficeSession(): Flow<OfficePresence?> {
        return officePresenceDao.getActiveSessions().map { sessions ->
            sessions.firstOrNull()?.let { OfficePresenceMapper.toDomain(it) }
        }
    }

    override fun getTodayTotalHours(): Flow<Float> {
        val today = java.time.LocalDate.now()
        val startOfDay = today.atStartOfDay().toString()
        val endOfDay = today.plusDays(1).atStartOfDay().toString()
        return officePresenceDao.getSessionsInRange(startOfDay, endOfDay).map { sessions ->
            sessions.sumOf { session ->
                if (session.exitTime != null) {
                    java.time.temporal.ChronoUnit.MINUTES.between(session.entryTime, session.exitTime).toDouble() / 60.0
                } else {
                    0.0
                }
            }.toFloat()
        }
    }
}
