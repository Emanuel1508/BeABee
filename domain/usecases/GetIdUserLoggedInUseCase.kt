package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository

class GetIdUserLoggedInUseCase (private val authenticationRepository: AuthenticationRepository) {
    operator fun invoke() = authenticationRepository.getIdUserLoggedIn()
}