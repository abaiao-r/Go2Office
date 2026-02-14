package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.OfficePresence
import com.example.go2office.domain.repository.OfficeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class GetActiveOfficeSessionUseCase @Inject constructor(
    private val repository: OfficeRepository
) {
    operator fun invoke(): Flow<OfficePresence?> {
        return repository.getActiveOfficeSession()
    }
}
