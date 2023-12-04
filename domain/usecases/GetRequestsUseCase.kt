package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.RequestsRepository

class GetRequestsUseCase(private val requestsRepository: RequestsRepository) {
    suspend operator fun invoke() = requestsRepository.getRequests()
}