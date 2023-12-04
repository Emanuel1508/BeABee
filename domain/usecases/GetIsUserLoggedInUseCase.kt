package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository

class GetIsUserLoggedInUseCase(private val authenticationRepository: AuthenticationRepository) {
    operator fun invoke() = authenticationRepository.getIsUserLoggedIn()
}