package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.DailyEntry
import com.example.go2office.domain.repository.OfficeRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
class GetDailyEntriesUseCase @Inject constructor(
    private val repository: OfficeRepository
) {
    fun forRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyEntry>> {
        return repository.getDailyEntriesInRange(startDate, endDate)
    }

    fun forMonth(yearMonth: YearMonth): Flow<List<DailyEntry>> {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return repository.getDailyEntriesInRange(startDate, endDate)
    }

    fun recent(limit: Int = 10): Flow<List<DailyEntry>> {
        return repository.getRecentEntries(limit)
    }
    suspend fun getByDate(date: LocalDate): DailyEntry? {
        return repository.getDailyEntry(date)
    }
}
