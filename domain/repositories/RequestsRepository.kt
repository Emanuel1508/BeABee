package com.ibm.internship.beabee.domain.repositories

import com.ibm.internship.beabee.domain.models.RequestDetailsResponse
import com.ibm.internship.beabee.domain.models.GetRequestResponse
import com.ibm.internship.beabee.domain.models.GetMyRequestResponse
import com.ibm.internship.beabee.domain.utils.UseCaseResponse

interface RequestsRepository {
    suspend fun submitFeedback(
        requestId: String,
        userId: String,
        rating: Float
    ): UseCaseResponse<Unit>

    suspend fun getMyRequests(idRequester: String): UseCaseResponse<List<GetMyRequestResponse>>
    suspend fun finishRequest(userId: String, requestId: String): UseCaseResponse<Unit>
    suspend fun getRequests(): UseCaseResponse<List<GetRequestResponse>>
    suspend fun getRequestDetails(requestId: String): UseCaseResponse<RequestDetailsResponse>
    suspend fun acceptRequest(requestId: String, userId: String): UseCaseResponse<Unit>
}