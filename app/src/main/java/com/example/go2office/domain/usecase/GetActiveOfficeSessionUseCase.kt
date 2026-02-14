package com.example.go2office.domain.usecase

import com.example.go2office.domain.model.OfficePresence
import com.example.go2office.domain.repository.OfficeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get the current active office session.
 * Returns Flow that emits the active session (where exitTime is null).
 */
class GetActiveOfficeSessionUseCase @Inject constructor(
    private val repository: OfficeRepository
) {
    operator fun invoke(): Flow<OfficePresence?> {
        return repository.getActiveOfficeSession()
    }
}


