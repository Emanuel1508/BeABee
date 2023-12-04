package com.ibm.internship.beabee.ui.dashboard.myprofile

sealed class ProfileDestination {
    data object LoginScreen : ProfileDestination()
    data object ManageAccount : ProfileDestination()
    data object AccountDetails : ProfileDestination()
}