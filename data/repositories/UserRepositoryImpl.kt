package com.ibm.beabee.data.repositories

import android.content.ContentValues
import com.ibm.beabee.data.network.UserApi
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.domain.models.UpdateUserParameters
import com.ibm.internship.beabee.domain.models.GetUserResponse
import com.ibm.internship.beabee.domain.repositories.UserRepository
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import java.net.UnknownHostException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    private val TAG = this::javaClass.name

    override suspend fun updateUser(
        updateUserParameters: UpdateUserParameters
    ): UseCaseResponse<Unit> {
        return try {
            val response = userApi.updateUser(
                userId = updateUserParameters.userId,
                name = updateUserParameters.name,
                email = updateUserParameters.email,
                phone = updateUserParameters.phone,
                tags = updateUserParameters.tags
            )
            BeeLog.d(TAG, "Get user succeeded")
            UseCaseResponse.Success(response)
        } catch (unknownHostException: UnknownHostException) {
            unknownHostException.getUserError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getUserError(ErrorMessage.GENERAL)
        }
    }

    override suspend fun getUser(idUser: String): UseCaseResponse<GetUserResponse> {
        return try {
            UseCaseResponse.Success(userApi.getUser(idUser))
        } catch (unknownHostException: UnknownHostException) {
            unknownHostException.getUserError(ErrorMessage.NO_NETWORK)
        } catch (exception: Exception) {
            exception.getUserError(ErrorMessage.NO_NETWORK)
        }
    }

    private fun java.lang.Exception.getUserError(error: ErrorMessage): UseCaseResponse.Failure {
        BeeLog.e(ContentValues.TAG, "Request failure. Exception: ${this.message}")
        return UseCaseResponse.Failure(error)
    }
}