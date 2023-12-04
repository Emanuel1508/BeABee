package com.ibm.internship.beabee.ui.dashboard.myprofile.accountdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.models.GetUserResponse
import com.ibm.internship.beabee.domain.usecases.GetUserUseCase
import com.ibm.internship.beabee.domain.usecases.GetIdUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.UpdateUserUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.domain.utils.splitFirstLastName
import com.ibm.internship.beabee.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val getUserDetailsUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserIdUseCase: GetIdUserLoggedInUseCase
) : BaseViewModel() {
    private var _userDetails = MutableLiveData<GetUserResponse>()
    val userDetails: LiveData<GetUserResponse> get() = _userDetails

    private val _navigation = SingleLiveEvent<AccountDetailsNavigation>()
    val navigation: SingleLiveEvent<AccountDetailsNavigation> = _navigation

    private var _isSaveButtonLoading = MutableLiveData<Boolean>()
    val isSaveButtonLoading: LiveData<Boolean> get() = _isSaveButtonLoading

    private val _isSaveButtonEnabled = MutableLiveData(false)
    val isSaveButtonEnabled: LiveData<Boolean> get() = _isSaveButtonEnabled

    private lateinit var initialFirstName: String
    private lateinit var initialLastName: String

    private companion object {
        private val TAG: String = this::class.java.simpleName
    }

    init {
        getUserAccountDetails()
    }

    fun getUserAccountDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            showLoading()
            when (val userId = getUserIdUseCase()) {
                is UseCaseResponse.Failure -> onGetDetailsFailure(userId.error)
                is UseCaseResponse.Success -> getAccountDetailsGivenId(userId.body)
            }
        }
    }

    private suspend fun getAccountDetailsGivenId(userId: String) {
        when (val response = getUserDetailsUseCase(userId)) {
            is UseCaseResponse.Success -> onGetDetailsSuccess(response.body)
            is UseCaseResponse.Failure -> onGetDetailsFailure(response.error)
        }
    }

    private fun onGetDetailsFailure(result: ErrorMessage) {
        hideLoading()
        showError(result)
        BeeLog.e(TAG, "Get details failed with exception: $result")
    }

    private fun onGetDetailsSuccess(details: GetUserResponse) {
        val name = details.name.splitFirstLastName()
        initialFirstName = name.first
        initialLastName = name.second
        _userDetails.postValue(details)
        hideLoading()
        BeeLog.e(TAG, "Get Details Success")
    }

    private fun updateUserDetails(firstName: String, lastName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaveButtonLoading.postValue(true)
            when (val userIdResponse = getUserIdUseCase()) {
                is UseCaseResponse.Success -> onUpdate(
                    userIdResponse.body,
                    firstName,
                    lastName
                )

                is UseCaseResponse.Failure -> showError(ErrorMessage.INVALID_USER)
            }
        }
    }

    private suspend fun onUpdate(userId: String, firstName: String, lastName: String) {
        when (val result =
            updateUserUseCase(
                userId = userId,
                firstName = firstName,
                lastName = lastName
            )
        ) {
            is UseCaseResponse.Success -> setNavigation(AccountDetailsNavigation.Profile)
            is UseCaseResponse.Failure -> showError(result.error)
        }
    }

    fun onEvent(event: AccountDetailsEvent) {
        when (event) {
            is AccountDetailsEvent.NameChanged -> setSaveButtonState(
                event.firstName,
                event.lastName
            )

            is AccountDetailsEvent.SaveChanges -> updateUserDetails(event.firstName, event.lastName)
        }
    }

    private fun setSaveButtonState(firstName: String, lastName: String) {
        val namesNotBlank = firstName.isNotBlank() && lastName.isNotBlank()
        val nameIsDifferent = firstName != initialFirstName || lastName != initialLastName
        _isSaveButtonEnabled.value = namesNotBlank && nameIsDifferent
    }

    private fun setNavigation(destination: AccountDetailsNavigation) =
        _navigation.postValue(destination)

    sealed class AccountDetailsNavigation {
        data object Profile : AccountDetailsNavigation()
    }
}