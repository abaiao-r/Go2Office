package com.example.go2office.domain.usecase
import com.example.go2office.domain.model.OfficeSettings
import com.example.go2office.domain.repository.OfficeRepository
import javax.inject.Inject
class SaveOfficeSettingsUseCase @Inject constructor(
    private val repository: OfficeRepository
) {
    suspend operator fun invoke(settings: OfficeSettings): Result<Unit> {
        return try {
            repository.saveSettings(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
