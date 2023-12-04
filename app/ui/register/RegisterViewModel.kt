package com.ibm.internship.beabee.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.usecases.UpdateUserUseCase
import com.ibm.internship.beabee.domain.usecases.UserRegisterUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.utils.ValidateFields
import com.ibm.internship.beabee.domain.utils.ValidationMessage
import com.ibm.internship.beabee.utils.CheckboxState
import com.ibm.internship.beabee.utils.isChecked
import com.ibm.internship.beabee.utils.TextFieldState
import com.ibm.internship.beabee.utils.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateFields: ValidateFields,
    private val registerUseCase: UserRegisterUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : BaseViewModel() {

    private val _firstNameState = MutableLiveData<TextFieldState>()
    val firstNameState: LiveData<TextFieldState> get() = _firstNameState

    private val _lastNameState = MutableLiveData<TextFieldState>()
    val lastNameState: LiveData<TextFieldState> get() = _lastNameState

    private val _emailState = MutableLiveData<TextFieldState>()
    val emailState: LiveData<TextFieldState> get() = _emailState

    private val _phoneState = MutableLiveData<TextFieldState>()
    val phoneState: LiveData<TextFieldState> get() = _phoneState

    private val _passwordState = MutableLiveData<TextFieldState>()
    val passwordState: LiveData<TextFieldState> get() = _passwordState

    private val _confirmPasswordState = MutableLiveData<TextFieldState>()
    val confirmPasswordState: LiveData<TextFieldState> get() = _confirmPasswordState

    private val _checkedTermsAndConditionState = MutableLiveData<CheckboxState>()
    val checkedTermsAndConditionState: LiveData<CheckboxState> get() = _checkedTermsAndConditionState

    private val _checkedPrivacyAndPolicyState = MutableLiveData<CheckboxState>()
    val checkedPrivacyAndPolicyState: LiveData<CheckboxState> get() = _checkedPrivacyAndPolicyState

    val registerButtonState = MediatorLiveData(false)

    private val TAG: String = this::class.java.simpleName

    init {
        val errorSources = listOf(
            firstNameState,
            lastNameState,
            emailState,
            phoneState,
            passwordState,
            confirmPasswordState,
            checkedTermsAndConditionState,
            checkedPrivacyAndPolicyState
        )
        errorSources.forEach { errorLiveData ->
            registerButtonState.addSource(errorLiveData) {
                isRegisterButtonActive()
            }
        }
    }

    private fun setFirstNameState(result: ValidationMessage, firstName: String) {
        _firstNameState.value = TextFieldState(
            text = firstName,
            error = result,
            isErrorIconVisible = showErrorIcon(firstName, result)
        )
    }

    private fun setLastNameState(result: ValidationMessage, lastName: String) {
        _lastNameState.value = TextFieldState(
            text = lastName,
            error = result,
            isErrorIconVisible = showErrorIcon(lastName, result)
        )
    }

    private fun setEmailState(result: ValidationMessage, email: String) {
        _emailState.value = TextFieldState(
            text = email,
            error = result,
            isErrorIconVisible = showErrorIcon(email, result)
        )
    }

    private fun setPhoneState(result: ValidationMessage, phone: String) {
        _phoneState.value = TextFieldState(
            text = phone,
            error = result,
            isErrorIconVisible = showErrorIcon(phone, result)
        )
    }

    private fun setPasswordState(result: ValidationMessage, password: String) {
        _passwordState.value = TextFieldState(
            text = password,
            error = result,
            isErrorIconVisible = showErrorIcon(password, result)
        )
    }

    private fun setConfirmPasswordState(result: ValidationMessage, confirmPassword: String) {
        _confirmPasswordState.value =
            TextFieldState(
                text = confirmPassword,
                error = result,
                isErrorIconVisible = showErrorIcon(confirmPassword, result)
            )
    }

    private fun setCheckedTermsAndCondState(termsAndCond: Boolean) {
        _checkedTermsAndConditionState.value = CheckboxState(termsAndCond)
    }

    private fun setPrivacyAndPolicyState(privacyAndPolicy: Boolean) {
        _checkedPrivacyAndPolicyState.value = CheckboxState(privacyAndPolicy)
    }

    fun onValidateFirstNameEvent(firstName: String) {
        val result = validateFields.validateFirstName(firstName)
        setFirstNameState(result, firstName)
    }

    fun onValidateLastNameEvent(lastName: String) {
        val result = validateFields.validateLastName(lastName)
        setLastNameState(result, lastName)
    }

    fun onValidateEmailEvent(email: String) {
        val result = validateFields.validateEmail(email)
        setEmailState(result, email)
    }

    fun onValidatePhoneEvent(phone: String) {
        val result = validateFields.validatePhone(phone)
        setPhoneState(result, phone)
    }

    fun onValidatePasswordEvent(password: String) {
        val result = validateFields.validatePassword(password)
        setPasswordState(result, password)
    }

    fun onValidateConfirmPasswordEvent(password: String, confirmPassword: String) {
        val result = validateFields.validateConfirmPassword(password, confirmPassword)
        setConfirmPasswordState(result, confirmPassword)
    }

    fun onTermsAndConditionsCheckEvent(isChecked: Boolean) {
        setCheckedTermsAndCondState(isChecked)
    }

    fun onPrivacyAndPolicyCheckEvent(isChecked: Boolean) {
        setPrivacyAndPolicyState(isChecked)
    }

    private fun showErrorIcon(text: String, resultedError: ValidationMessage) =
        text.isNotEmpty() && resultedError != ValidationMessage.ERROR_NOT_FOUND

    private fun isRegisterButtonActive() {
        registerButtonState.value = firstNameState.value?.isValid() == true &&
                lastNameState.value?.isValid() == true &&
                emailState.value?.isValid() == true &&
                phoneState.value?.isValid() == true &&
                passwordState.value?.isValid() == true &&
                confirmPasswordState.value?.isValid() == true &&
                checkedTermsAndConditionState.value?.isChecked() == true &&
                checkedPrivacyAndPolicyState.value?.isChecked() == true
    }

    fun onRegisterButtonPressed(email: String, password: String) {
        viewModelScope.launch {
            registerUser(email, password)
        }
    }

    private suspend fun registerUser(email: String, password: String) {
        showLoading()
        when (val result = registerUseCase(email, password)) {
            is UseCaseResponse.Success -> updateUser(result.body)
            is UseCaseResponse.Failure -> showErrorMessage(
                errorMessage = result.error,
                logMessage = "Register failed with exception: $result"
            )
        }
    }

    private suspend fun updateUser(userId: String) {
        if (isDataNotNull()) {
            val updateUserResult = updateUserUseCase(
                userId = userId,
                firstName = _firstNameState.value!!.text,
                lastName = _lastNameState.value!!.text,
                email = _emailState.value!!.text,
                phone = _phoneState.value!!.text
            )
            showUpdateResponse(updateUserResult)
        } else {
            showErrorMessage(
                errorMessage = ErrorMessage.INVALID_USER,
                logMessage = "Update user failed: invalid fields"
            )
        }
    }

    private fun isDataNotNull(): Boolean {
        return _firstNameState.value != null &&
                _lastNameState.value != null &&
                _emailState.value != null &&
                _phoneState.value != null
    }

    private fun showUpdateResponse(response: UseCaseResponse<Unit>) {
        when (response) {
            is UseCaseResponse.Success -> onSuccess(response)
            is UseCaseResponse.Failure -> showErrorMessage(
                errorMessage = response.error,
                logMessage = "Update user failed with exception: ${response.error}"
            )
        }
    }

    private fun showErrorMessage(errorMessage: ErrorMessage, logMessage: String) {
        hideLoading()
        showError(errorMessage)
        BeeLog.e(TAG, logMessage)
    }

    private fun onSuccess(response: UseCaseResponse<Unit>) {
        hideLoading()
        showSuccess(response)
        BeeLog.d(TAG, "Update user success")
    }
}