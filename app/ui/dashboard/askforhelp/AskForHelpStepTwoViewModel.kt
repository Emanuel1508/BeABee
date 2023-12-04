package com.ibm.internship.beabee.ui.dashboard.askforhelp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.utils.ButtonState
import com.ibm.internship.beabee.utils.Constants.Companion.EMPTY_STRING
import com.ibm.internship.beabee.utils.DecodeLocation
import com.ibm.internship.beabee.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskForHelpStepTwoViewModel @Inject constructor(
    private val decodeLocation: DecodeLocation,
) : BaseViewModel() {

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _locationPermissionState = SingleLiveEvent<LocationPermissionState>()
    val locationPermissionState: SingleLiveEvent<LocationPermissionState> = _locationPermissionState

    private val _locateMeButtonState = MutableLiveData<ButtonState>()
    val locateMeButtonState: LiveData<ButtonState> = _locateMeButtonState

    private var _nextButtonState = MutableLiveData<ButtonState>()
    val nextButtonState: LiveData<ButtonState> = _nextButtonState

    private val _navigation = SingleLiveEvent<NavDirections>()
    val navigation: SingleLiveEvent<NavDirections> = _navigation

    fun onLocationPermissionChange(granted: Boolean, wasDenied: Boolean = false) {
        if (granted || !wasDenied) {
            getUserLocation()
            _locateMeButtonState.value = ButtonState.Enabled
        } else {
            _location.value = EMPTY_STRING
            _locateMeButtonState.value = ButtonState.Disabled
        }
    }

    private fun getUserLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = decodeLocation.getUserLocation()) {
                is UseCaseResponse.Success -> onSuccess(response.body)
                is UseCaseResponse.Failure -> onFailure(response.error)
            }
        }
    }

    private fun onFailure(error: ErrorMessage) {
        showError(error)
    }

    private fun onSuccess(location: String) {
        setLocation(location)
        enableNextButton(location)
    }

    private fun setLocation(location: String) = _location.postValue(location)

    fun onLocateMeButtonClicked() {
        _locationPermissionState.value = LocationPermissionState.CheckPermission
    }

    fun enableNextButton(location: String) =
        _nextButtonState.postValue(
            if (location.isEmpty()) ButtonState.Disabled else ButtonState.Enabled
        )

    fun onNextButtonClicked() {
        _navigation.value =
            AskForHelpStepTwoFragmentDirections.actionAskForHelpStepTwoFragmentToAskForHelpStepThreeFragment()
    }

    fun onPreviousButtonClicked() {
        _navigation.value =
            AskForHelpStepTwoFragmentDirections.actionAskForHelpStepTwoFragmentToAskForHelpStepOneFragment()
    }

    sealed class LocationPermissionState {
        object CheckPermission : LocationPermissionState()
    }
}