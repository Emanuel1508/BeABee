package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.RequestsRepository

class SubmitFeedbackUseCase(private val repository: RequestsRepository) {

    suspend operator fun invoke(
        requestId: String,
        userId: String,
        rating: Float
    ) = repository.submitFeedback(
        requestId,
        userId,
        rating
    )
}