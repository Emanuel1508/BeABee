package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository

class DeactivateAccountUseCase(private val repository: AuthenticationRepository) {

    suspend operator fun invoke() = repository.deactivateAccount()
}