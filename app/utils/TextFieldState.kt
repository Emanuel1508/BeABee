package com.ibm.internship.beabee.utils

import com.ibm.internship.beabee.domain.utils.ValidationMessage
import kotlin.Unit.toString

data class TextFieldState(
    var text: String = toString(),
    var error: ValidationMessage = ValidationMessage.NOT_CHECKED,
    var isErrorIconVisible: Boolean = false
)

fun TextFieldState.isValid() = this.error == ValidationMessage.ERROR_NOT_FOUND