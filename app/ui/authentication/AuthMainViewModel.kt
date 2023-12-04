package com.ibm.internship.beabee.ui.authentication

import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.utils.SingleLiveEvent

class AuthMainViewModel : BaseViewModel() {
    private val _navigation = SingleLiveEvent<NavDestination>()
    val navigation: SingleLiveEvent<NavDestination> get() = _navigation

    fun onCreateAccountButtonClick() {
        _navigation.value = NavDestination.Register
    }

    fun onLoginButtonClick() {
        _navigation.value = NavDestination.Login
    }

    sealed class NavDestination {
        object Login : NavDestination()
        object Register : NavDestination()
    }
}