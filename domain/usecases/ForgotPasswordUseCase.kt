package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.AuthenticationRepository
import com.ibm.internship.beabee.domain.utils.ValidationMessage

class ForgotPasswordUseCase(private val authenticationRepository: AuthenticationRepository) {

    operator fun invoke(email: String, validationMessage: ValidationMessage) =
        authenticationRepository.forgotPassword(email, validationMessage)
}