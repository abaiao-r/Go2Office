package com.example.go2office.domain.repository
import com.example.go2office.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth
interface OfficeRepository {
    fun getSettings(): Flow<OfficeSettings?>
    suspend fun getSettingsOnce(): OfficeSettings?
    suspend fun saveSettings(settings: OfficeSettings): Result<Unit>
    suspend fun getDailyEntry(date: LocalDate): DailyEntry?
    fun getDailyEntryFlow(date: LocalDate): Flow<DailyEntry?>
    fun getDailyEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyEntry>>
    suspend fun getDailyEntriesInRangeOnce(startDate: LocalDate, endDate: LocalDate): List<DailyEntry>
    suspend fun getOfficeDaysInRange(startDate: LocalDate, endDate: LocalDate): List<DailyEntry>
    fun getRecentEntries(limit: Int = 10): Flow<List<DailyEntry>>
    suspend fun saveDailyEntry(entry: DailyEntry): Result<Unit>
    suspend fun deleteDailyEntry(date: LocalDate): Result<Unit>
    suspend fun getMonthlyLog(yearMonth: YearMonth): MonthlyLog?
    fun getMonthlyLogFlow(yearMonth: YearMonth): Flow<MonthlyLog?>
    fun getAllMonthlyLogs(): Flow<List<MonthlyLog>>
    suspend fun saveMonthlyLog(log: MonthlyLog): Result<Unit>
    suspend fun getHoliday(date: LocalDate): Holiday?
    fun getHolidaysInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Holiday>>
    suspend fun getHolidaysInRangeOnce(startDate: LocalDate, endDate: LocalDate): List<Holiday>
    fun getAllHolidays(): Flow<List<Holiday>>
    suspend fun saveHoliday(holiday: Holiday): Result<Unit>
    suspend fun deleteHoliday(date: LocalDate): Result<Unit>
    fun getActiveOfficeSession(): Flow<OfficePresence?>
    fun getTodayTotalHours(): Flow<Float>
}
