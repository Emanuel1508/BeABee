package com.ibm.internship.beabee.utils

import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.domain.utils.ValidationMessage

fun ValidationMessage.mapToPresentation(): Int {
    return when (this) {
        ValidationMessage.PASSWORD_EMPTY -> R.string.empty_password_error
        ValidationMessage.PASSWORD_INVALID -> R.string.invalid_password_error
        ValidationMessage.PHONE_EMAIL_EMPTY -> R.string.empty_email_or_phone_error
        ValidationMessage.EMAIL_INVALID -> R.string.invalid_email_error
        ValidationMessage.NOT_CHECKED -> R.string.err_none
        ValidationMessage.FIRST_NAME_INVALID -> R.string.invalid_first_name_error
        ValidationMessage.LAST_NAME_INVALID -> R.string.invalid_last_name_error
        ValidationMessage.PASSWORDS_UNMATCHED -> R.string.passwords_do_not_match_error
        ValidationMessage.PHONE_INVALID -> R.string.incorrect_phone_error
        else -> R.string.err_none
    }
}