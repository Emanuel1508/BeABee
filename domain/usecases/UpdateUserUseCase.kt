package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.UserRepository
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.domain.models.UpdateUserParameters

class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        userId: String,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phone: String? = null,
        tags: ArrayList<String>? = null
    ): UseCaseResponse<Unit> {
        val name = if (firstName != null && lastName != null) "$firstName $lastName" else null
        val updateUserParameters = UpdateUserParameters(
            userId = userId,
            name = name,
            email = email,
            phone = phone,
            tags = tags
        )
        return userRepository.updateUser(updateUserParameters)
    }
}