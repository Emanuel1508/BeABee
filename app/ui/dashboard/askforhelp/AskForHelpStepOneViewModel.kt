package com.ibm.internship.beabee.ui.dashboard.askforhelp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.usecases.AnalyzeTextUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.utils.ButtonState
import com.ibm.internship.beabee.utils.Constants
import com.ibm.internship.beabee.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskForHelpStepOneViewModel @Inject constructor(
    private val analyzeText: AnalyzeTextUseCase
) : BaseViewModel() {

    private val TAG: String = this::class.java.simpleName

    private var _analyzeButtonState = MutableLiveData<ButtonState>()
    val analyzeButtonState: LiveData<ButtonState> = _analyzeButtonState

    private var _chipTags = MutableLiveData<List<String>>()
    val chipTags: LiveData<List<String>> = _chipTags

    private var _nextButtonState = SingleLiveEvent<ButtonState>()
    val nextButtonState: SingleLiveEvent<ButtonState> = _nextButtonState

    private val _navigation = SingleLiveEvent<NavDirections>()
    val navigation: SingleLiveEvent<NavDirections> = _navigation

    private var checkedChips = mutableListOf<String>()

    fun onCheckedStateChanged(tagText: String, isChecked: Boolean) {
        checkedChips.apply {
            if (isChecked) add(tagText) else remove(tagText)
        }
        enableNextButton()
    }

    fun onDescriptionTextChange(description: String) {
        _analyzeButtonState.value =
            if (description.trim().isNotEmpty()) ButtonState.Enabled
            else ButtonState.Disabled
    }

    fun onAnalyzeButtonClick(descriptionText: String) {
        showLoading()
        resetResults()
        analyzeText(descriptionText)
    }

    fun onNextButtonClick() {
        _navigation.value = AskForHelpStepOneFragmentDirections
            .actionAskForHelpStepOneFragmentToAskForHelpStepTwoFragment()
    }

    private fun resetResults() {
        _chipTags.value = emptyList()
    }

    private fun analyzeText(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = analyzeText(text, Constants.MAX_RESULTS)) {
                is UseCaseResponse.Success -> onSuccess(result.body)
                is UseCaseResponse.Failure -> onFailure(result.error)
            }
        }
    }

    private fun enableNextButton() {
        _nextButtonState.value = if (checkedChips.isNotEmpty()) ButtonState.Enabled
        else ButtonState.Disabled
    }

    private fun onSuccess(tagList: List<String>) {
        hideLoading()
        _chipTags.postValue(tagList)
        BeeLog.d(TAG, "Analyze success")
    }

    private fun onFailure(error: ErrorMessage) {
        hideLoading()
        _chipTags.postValue(emptyList())
        showError(error)
        BeeLog.e(TAG, "Analyze error: $error")
    }
}