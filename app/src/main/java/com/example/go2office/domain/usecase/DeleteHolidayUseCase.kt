package com.example.go2office.domain.usecase
import com.example.go2office.domain.repository.OfficeRepository
import java.time.LocalDate
import javax.inject.Inject
class DeleteHolidayUseCase @Inject constructor(
    private val repository: OfficeRepository
) {
    suspend operator fun invoke(date: LocalDate): Result<Unit> {
        return repository.deleteHoliday(date)
    }
}
