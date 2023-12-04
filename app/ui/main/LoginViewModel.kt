package com.ibm.internship.beabee.ui.main

import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.usecases.GetIsUserLoggedInUseCase
import com.ibm.internship.beabee.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    getIsUserLoggedInUseCase: GetIsUserLoggedInUseCase
) : BaseViewModel() {
    private val _navigation = SingleLiveEvent<LoginDestination>()
    val navigation: SingleLiveEvent<LoginDestination> = _navigation

    init {
        if (getIsUserLoggedInUseCase()) {
            _navigation.value = LoginDestination.Main
        }
    }

    sealed class LoginDestination {
        object Main : LoginDestination()
    }
}