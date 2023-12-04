package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.RequestsRepository

class FinishRequestUseCase(private val repository: RequestsRepository) {
    suspend operator fun invoke(
        userId: String,
        requestId: String
    ) = repository.finishRequest(
        userId,
        requestId
    )
}
