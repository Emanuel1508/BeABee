package com.ibm.internship.beabee.utils

import com.ibm.internship.beabee.domain.utils.ValidationMessage
import javax.inject.Inject

class ValidateFields @Inject constructor() {
    fun validateFirstName(firstName: String): ValidationMessage {
        return if (!firstName.matches(Constants.NAME_REGEX.toRegex())) ValidationMessage.FIRST_NAME_INVALID
        else ValidationMessage.ERROR_NOT_FOUND
    }

    fun validateLastName(lastName: String): ValidationMessage {
        return if (!lastName.matches(Constants.NAME_REGEX.toRegex())) ValidationMessage.LAST_NAME_INVALID
        else ValidationMessage.ERROR_NOT_FOUND
    }

    fun validateEmail(email: String): ValidationMessage {
        return if (!email.matches(Constants.EMAIL_REGEX.toRegex()) && email.length <= Constants.REGISTER_FIELDS_MAX_LENGTH
        ) ValidationMessage.EMAIL_INVALID
        else ValidationMessage.ERROR_NOT_FOUND
    }

    fun validatePhone(phone: String): ValidationMessage {
        return if (!phone.matches(Constants.PHONE_REGEX.toRegex())) ValidationMessage.PHONE_INVALID
        else ValidationMessage.ERROR_NOT_FOUND
    }

    fun validatePassword(password: String): ValidationMessage {
        return if (!password.matches(Constants.PASSWORD_REGEX.toRegex())) ValidationMessage.PASSWORD_INVALID
        else ValidationMessage.ERROR_NOT_FOUND
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationMessage {
        return if (password != confirmPassword) ValidationMessage.PASSWORDS_UNMATCHED
        else ValidationMessage.ERROR_NOT_FOUND
    }
}