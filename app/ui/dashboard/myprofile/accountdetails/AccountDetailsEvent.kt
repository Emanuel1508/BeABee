package com.ibm.internship.beabee.ui.dashboard.myprofile.accountdetails

sealed class AccountDetailsEvent {
    data class NameChanged(
        val firstName: String,
        val lastName: String
    ) : AccountDetailsEvent()

    data class SaveChanges(
        val firstName: String,
        val lastName: String,
    ) : AccountDetailsEvent()
}