package com.ibm.internship.beabee.domain.repositories

import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.domain.utils.ValidationMessage

interface AuthenticationRepository {
    suspend fun registerUser(
        email: String,
        password: String,
    ): UseCaseResponse<String>

    suspend fun loginUser(
        email: String,
        password: String,
    ): UseCaseResponse<Unit>

    suspend fun logoutUser(): UseCaseResponse<Unit>

    suspend fun deactivateAccount(): UseCaseResponse<Unit>

    fun getIdUserLoggedIn(): UseCaseResponse<String>

    fun getIsUserLoggedIn(): Boolean

    fun forgotPassword(email: String, validationMessage: ValidationMessage): UseCaseResponse<String>
}