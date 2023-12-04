package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.UserRepository

class GetUserUseCase(private val getUserRepository: UserRepository) {
    suspend operator fun invoke(idUser: String) = getUserRepository.getUser(idUser)
}