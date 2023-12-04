package com.ibm.internship.beabee.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.usecases.ForgotPasswordUseCase
import com.ibm.internship.beabee.domain.usecases.GetIdUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.GetUserUseCase
import com.ibm.internship.beabee.domain.usecases.UserLoginUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.domain.utils.ValidationMessage
import com.ibm.internship.beabee.utils.ButtonState
import com.ibm.internship.beabee.utils.SingleLiveEvent
import com.ibm.internship.beabee.utils.TextFieldState
import com.ibm.internship.beabee.utils.ValidateFields
import com.ibm.internship.beabee.utils.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val loginUseCase: UserLoginUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getIdUserLoggedInUseCase: GetIdUserLoggedInUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val validation: ValidateFields
) : BaseViewModel() {
    private val _isLoginButtonEnabled: MutableLiveData<ButtonState> = MutableLiveData()
    val isLoginButtonEnabled: LiveData<ButtonState> = _isLoginButtonEnabled

    private var _passwordState: MutableLiveData<TextFieldState> =
        MutableLiveData<TextFieldState>(TextFieldState())
    val passwordState: LiveData<TextFieldState> = _passwordState

    private var _emailOrPhoneState: MutableLiveData<TextFieldState> =
        MutableLiveData<TextFieldState>(TextFieldState())
    val emailOrPhoneState: LiveData<TextFieldState> = _emailOrPhoneState

    private val _navigation = SingleLiveEvent<NavDestination>()
    val navigation: LiveData<NavDestination> get() = _navigation

    companion object{
        private val TAG: String = this::class.java.simpleName
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailOrPhoneChanged -> validateEmailOrPhone(event.emailOrPhone)
            is LoginEvent.PasswordChanged -> validatePassword(event.password)
            is LoginEvent.Submit -> loginUser(event.email, event.password)
        }
        setLoginButtonState()
    }

    fun onForgotPasswordButtonPress(email: String) {
        val validationResult = validation.validateEmail(email)
        when (val response = forgotPasswordUseCase(email, validationResult)) {
            is UseCaseResponse.Success -> showSuccess(response.body)
            is UseCaseResponse.Failure -> onFailure(response.error)
        }
    }

    private fun validateEmailOrPhone(emailOrPhone: String) {
        val result = validateText(
            text = emailOrPhone,
            errorMessage = ValidationMessage.PHONE_EMAIL_EMPTY
        )
        setEmailOrPhoneState(result, emailOrPhone)
    }

    private fun validatePassword(password: String) {
        val result = validateText(
            text = password,
            errorMessage = ValidationMessage.PASSWORD_EMPTY
        )
        setPasswordState(result, password)
    }

    private fun validateText(text: String, errorMessage: ValidationMessage) =
        if (text.trim().isEmpty()) {
            errorMessage
        } else {
            ValidationMessage.ERROR_NOT_FOUND
        }

    private fun loginUser(email: String, password: String) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = loginUseCase(email, password)) {
                is UseCaseResponse.Success -> onFirebaseLoginResponse()
                is UseCaseResponse.Failure -> onFailure(result.error)
            }
        }
    }

    private suspend fun onFirebaseLoginResponse() {
        when (val resultGetUserId = getIdUserLoggedInUseCase()) {
            is UseCaseResponse.Success -> getUser(resultGetUserId.body)
            is UseCaseResponse.Failure -> showError(resultGetUserId.error)
        }
    }

    private suspend fun getUser(idUser: String) {
        when (val userResponse = getUserUseCase(idUser)) {
            is UseCaseResponse.Success -> onTagsFetchingSuccess(userResponse.body.tags)
            is UseCaseResponse.Failure -> onFailure(userResponse.error)
        }
    }

    private fun onTagsFetchingSuccess(tags: List<String>?) {
        hideLoading()
        chooseLoginDestination(tags.isNullOrEmpty())
        BeeLog.d(TAG, "Tags retrieved successfully")
    }

    private fun onFailure(result: ErrorMessage) {
        hideLoading()
        showError(result)
    }

    private fun chooseLoginDestination(isUserTagsEmptyOrNull: Boolean) =
        _navigation.postValue(if (isUserTagsEmptyOrNull) NavDestination.Description else NavDestination.Main)

    private fun setPasswordState(result: ValidationMessage, password: String) {
        _passwordState.value = TextFieldState(
            text = password,
            error = result,
            isErrorIconVisible = showErrorIcon(password, result)
        )
    }

    private fun setEmailOrPhoneState(result: ValidationMessage, email: String) {
        _emailOrPhoneState.value = TextFieldState(
            text = email,
            error = result,
            isErrorIconVisible = showErrorIcon(email, result)
        )
    }

    private fun setLoginButtonState() {
        if (_passwordState.value != null && _emailOrPhoneState.value != null) {
            _isLoginButtonEnabled.value =
                if (_passwordState.value!!.isValid() && _emailOrPhoneState.value!!.isValid())
                    ButtonState.Enabled else ButtonState.Disabled
        }
    }

    private fun showErrorIcon(text: String, resultedError: ValidationMessage) =
        text.isNotEmpty() && resultedError != ValidationMessage.ERROR_NOT_FOUND

    sealed class NavDestination {
        data object Main : NavDestination()
        data object Description : NavDestination()
    }
}