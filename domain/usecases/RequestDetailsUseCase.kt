package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.RequestsRepository

class RequestDetailsUseCase(
    private val requestDetailsRepository: RequestsRepository
) {
    suspend fun getRequestDetails(requestId: String) =
        requestDetailsRepository.getRequestDetails(requestId)
}