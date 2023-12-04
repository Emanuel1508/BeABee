package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.RequestsRepository

class GetMyRequestsUseCase(private val requestsRepository: RequestsRepository) {
    suspend operator fun invoke(idRequester: String) =
        requestsRepository.getMyRequests(idRequester)
}