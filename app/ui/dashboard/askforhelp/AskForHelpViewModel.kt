package com.ibm.internship.beabee.ui.dashboard.askforhelp

import androidx.navigation.NavDirections
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AskForHelpViewModel @Inject constructor() : BaseViewModel() {
    private val _navigation = SingleLiveEvent<NavDirections>()
    val navigation: SingleLiveEvent<NavDirections> = _navigation

    fun onRequestHelpButtonClick() {
        _navigation.value = AskForHelpFragmentDirections
            .actionAskForHelpFragmentToAskForHelpStepOneFragment()
    }
}