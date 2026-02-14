package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.OfficeSettings
import com.example.go2office.domain.repository.OfficeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class GetOfficeSettingsUseCase @Inject constructor(
    private val repository: OfficeRepository
) {
    operator fun invoke(): Flow<OfficeSettings?> {
        return repository.getSettings()
    }
    suspend fun once(): OfficeSettings? {
        return repository.getSettingsOnce()
    }
}
