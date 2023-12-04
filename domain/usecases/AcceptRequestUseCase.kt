package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.RequestsRepository

class AcceptRequestUseCase(private val requestsRepository: RequestsRepository) {
    suspend operator fun invoke(requestId: String, userId: String) =
        requestsRepository.acceptRequest(requestId, userId)
}