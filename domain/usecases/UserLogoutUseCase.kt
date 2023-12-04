package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository

class UserLogoutUseCase(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke() = authenticationRepository.logoutUser()
}