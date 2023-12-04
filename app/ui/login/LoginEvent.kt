package com.ibm.internship.beabee.ui.login

sealed class LoginEvent {
    data class EmailOrPhoneChanged(val emailOrPhone: String) : LoginEvent()

    data class PasswordChanged(val password: String) : LoginEvent()

    data class Submit(
        val email: String,
        val password: String,
    ) : LoginEvent()
}
