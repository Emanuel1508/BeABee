package com.ibm.internship.beabee.ui.description

import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.usecases.AnalyzeTextUseCase
import com.ibm.internship.beabee.domain.usecases.GetIdUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.GetUserUseCase
import com.ibm.internship.beabee.domain.usecases.UpdateUserUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.utils.ButtonState
import com.ibm.internship.beabee.utils.Constants.Companion.MAX_RESULTS
import com.ibm.internship.beabee.utils.SingleLiveEvent
import com.ibm.internship.beabee.utils.VisibilityState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDescriptionViewModel @Inject constructor(
    private val analyzeText: AnalyzeTextUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getIdUserLoggedInUseCase: GetIdUserLoggedInUseCase,
) : BaseViewModel() {

    private var _analyzeButtonState = MutableLiveData<ButtonState>()
    val analyzeButtonState: LiveData<ButtonState> get() = _analyzeButtonState

    private var _greetingName = MutableLiveData<Spanned>()
    val greetingName: LiveData<Spanned> get() = _greetingName

    private var _tagTextList = MutableLiveData<List<String>?>()
    val tagTextList: LiveData<List<String>?> get() = _tagTextList

    private var _nextButtonState = MutableLiveData<ButtonState>()
    val nextButtonState: LiveData<ButtonState> get() = _nextButtonState

    private val _updateUserTagsResult = SingleLiveEvent<UseCaseResponse<Unit>>()
    val updateUserTagsResult: LiveData<UseCaseResponse<Unit>> get() = _updateUserTagsResult

    private var _isNextLoading = MutableLiveData<VisibilityState>()
    val isNextLoading: LiveData<VisibilityState> get() = _isNextLoading

    private var selectedChips = mutableListOf<String>()
    companion object {
        private val TAG: String = this::class.java.simpleName
    }

    fun validateText(text: String) {
        _analyzeButtonState.value = if (text.trim().isNotEmpty()) ButtonState.Enabled
        else ButtonState.Disabled
    }

    fun onSetGreetingText(initialString: String, defaultName: String) {
        showLoading()
        when (val resultGetId = getIdUserLoggedInUseCase()) {
            is UseCaseResponse.Success -> fetchUserName(
                initialString,
                defaultName,
                resultGetId.body
            )
            is UseCaseResponse.Failure -> showError(resultGetId.error)
        }
    }

    fun onAnalyzeButtonClick(descriptionText: String) {
        showLoading()
        resetResults()
        analyzeText(descriptionText)
    }

    private fun fetchUserName(initialString: String, defaultName: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val userResponse = getUserUseCase(userId)) {
                is UseCaseResponse.Success -> setGreetingName(initialString, userResponse.body.name)
                is UseCaseResponse.Failure -> setGreetingName(initialString, defaultName)
            }
            hideLoading()
        }
    }

    private fun setGreetingName(initialString: String, userName: String) {
        val formattedText = String.format(initialString, userName)
        val finalGreetingText = Html.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        _greetingName.postValue(finalGreetingText)
    }

    private fun analyzeText(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = analyzeText(text, MAX_RESULTS)) {
                is UseCaseResponse.Success -> onAnalyzeSuccess(result.body)
                is UseCaseResponse.Failure -> onAnalyzeFailure(result.error)
            }
        }
    }

    private fun onAnalyzeSuccess(tagList: List<String>) {
        hideLoading()
        _tagTextList.postValue(tagList)
        BeeLog.d(TAG, "Analyze success")
    }

    private fun onAnalyzeFailure(error: ErrorMessage) {
        hideLoading()
        showError(error)
        BeeLog.e(TAG, "Analyze error: $error")
    }

    fun onChipCheckedChanged(isChecked: Boolean, chipText: String) {
        selectedChips.apply {
            if (isChecked) add(chipText) else remove(chipText)
        }
        _nextButtonState.value = if (selectedChips.isNotEmpty()) ButtonState.Enabled
        else ButtonState.Disabled

    }

    fun onNextButtonClicked() {
        viewModelScope.launch {
            _isNextLoading.postValue(VisibilityState.IsVisible)
            val userId = when (val userIdResponse = getIdUserLoggedInUseCase()) {
                is UseCaseResponse.Success -> userIdResponse.body
                is UseCaseResponse.Failure -> {
                    _updateUserTagsResult.value = UseCaseResponse.Failure(ErrorMessage.INVALID_USER)
                    return@launch
                }
            }
            val tags = ArrayList(selectedChips)
            val result = updateUserUseCase(userId = userId, tags = tags)
            _updateUserTagsResult.value = result
            _isNextLoading.postValue(VisibilityState.NotVisible)
        }
    }

    private fun resetResults() = _tagTextList.postValue(emptyList())
}