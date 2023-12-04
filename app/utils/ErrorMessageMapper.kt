


package com.ibm.internship.beabee.utils

import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.domain.utils.ErrorMessage

fun ErrorMessage.mapToPresentation(): Int {
    return when (this) {
        ErrorMessage.NO_NETWORK -> R.string.no_internet_error
        ErrorMessage.UPDATE_USER -> R.string.update_user_error
        ErrorMessage.INVALID_USER -> R.string.invalid_user_error
        ErrorMessage.INVALID_EMAIL -> R.string.email_already_used_error
        ErrorMessage.INCORRECT_ACCOUNT -> R.string.incorrect_account_error
        ErrorMessage.NOT_ENOUGH_TEXT -> R.string.not_enough_text_error
        ErrorMessage.UNSUPPORTED_LANGUAGE -> R.string.unsupported_language_error
        ErrorMessage.WATSON_GENERAL_MESSAGE -> R.string.general_message
        ErrorMessage.GENERAL -> R.string.general_error
        ErrorMessage.INCORRECT_EMAIL -> R.string.incorrect_email
        else -> R.string.err_none
    }
}