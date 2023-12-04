package com.ibm.beabee.data.repositories

import com.ibm.beabee.data.network.RequestApi
import com.ibm.beabee.data.network.models.toGetRequestResponse
import com.ibm.beabee.data.network.models.toRequestResponseDomain
import com.ibm.beabee.data.network.models.toMyRequestResponseDomain
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.domain.repositories.RequestsRepository
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import java.net.UnknownHostException
import javax.inject.Inject

class RequestsRepositoryImpl @Inject constructor(
    private val requestApi: RequestApi
) : RequestsRepository {
    private val TAG = this::class.simpleName

    override suspend fun getRequests() =
        try {
            val response = requestApi.getAllPendingRequests()
            BeeLog.d(TAG, "Get all pending was successful")
            UseCaseResponse.Success(response.toRequestResponseDomain())
        } catch (unknownHostException: UnknownHostException) {
            unknownHostException.getRequestError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getRequestError(ErrorMessage.GENERAL)
        }

    override suspend fun getRequestDetails(requestId: String) =
        try {
            val response = requestApi.getRequestDetails(requestId)
            BeeLog.d(TAG, "Get request details was successful")
            UseCaseResponse.Success(response.toGetRequestResponse())
        } catch (unknownHostException: UnknownHostException) {
            unknownHostException.getRequestError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getRequestError(ErrorMessage.GENERAL)
        }

    override suspend fun getMyRequests(idRequester: String) =
        try {
            val response = requestApi.getMyRequests(idRequester)
            BeeLog.d(TAG, "Get my requests succeeded")
            UseCaseResponse.Success(response.toMyRequestResponseDomain())
        } catch (unknownHostException: UnknownHostException) {
            unknownHostException.getRequestError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getRequestError(ErrorMessage.GENERAL)
        }

    override suspend fun submitFeedback(
        requestId: String,
        userId: String,
        rating: Float
    ) = try {
        requestApi.submitFeedback(requestId, userId, rating)
        BeeLog.d(TAG, "Submit rating succeeded")
        UseCaseResponse.Success(Unit)
    } catch (unknownHostException: UnknownHostException) {
        unknownHostException.getRequestError(ErrorMessage.NO_NETWORK)
    } catch (exception: Exception) {
        exception.getRequestError(ErrorMessage.GENERAL)
    }

    override suspend fun acceptRequest(requestId: String, userId: String) =
        try {
            requestApi.activateRequest(requestId, userId)
            UseCaseResponse.Success(Unit)
        } catch (unknownHostException: UnknownHostException) {
            UseCaseResponse.Failure(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            UseCaseResponse.Failure(ErrorMessage.GENERAL)
        }

    override suspend fun finishRequest(userId: String, requestId: String) =
        try {
            requestApi.finishRequest(requestId, userId)
            BeeLog.d(TAG, "Finish request succeeded")
            UseCaseResponse.Success(Unit)
        } catch (unknownHostException: UnknownHostException) {
            unknownHostException.getRequestError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getRequestError(ErrorMessage.GENERAL)
        }

    private fun java.lang.Exception.getRequestError(error: ErrorMessage): UseCaseResponse.Failure {
        BeeLog.e(TAG, "Error message: $error. Exception: ${this.message}")
        return UseCaseResponse.Failure(error)
    }
}