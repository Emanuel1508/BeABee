package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository

class UserRegisterUseCase(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(email: String, password: String) =
        authenticationRepository.registerUser(email, password)
}