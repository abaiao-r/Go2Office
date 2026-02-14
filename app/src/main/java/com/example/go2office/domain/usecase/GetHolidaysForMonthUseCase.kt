package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.Holiday
import com.example.go2office.domain.repository.OfficeRepository
import com.example.go2office.util.DateUtils
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import javax.inject.Inject

/**
 * Use case to get holidays for a specific month.
 */
class GetHolidaysForMonthUseCase @Inject constructor(
    private val repository: OfficeRepository
) {

    operator fun invoke(yearMonth: YearMonth): Flow<List<Holiday>> {
        val (startDate, endDate) = DateUtils.getMonthBounds(yearMonth)
        return repository.getHolidaysInRange(startDate, endDate)
    }
}

