package com.ibm.internship.beabee.domain.repositories

import com.ibm.internship.beabee.domain.models.GetUserResponse
import com.ibm.internship.beabee.domain.models.UpdateUserParameters
import com.ibm.internship.beabee.domain.utils.UseCaseResponse

interface UserRepository {
    suspend fun updateUser(
        updateUserParameters: UpdateUserParameters
    ): UseCaseResponse<Unit>

    suspend fun getUser(idUser: String): UseCaseResponse<GetUserResponse>
}