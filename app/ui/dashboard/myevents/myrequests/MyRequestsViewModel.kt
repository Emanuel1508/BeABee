package com.ibm.internship.beabee.ui.dashboard.myevents.myrequests

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.models.GetMyRequestResponse
import com.ibm.internship.beabee.domain.usecases.GetIdUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.GetMyRequestsUseCase
import com.ibm.internship.beabee.domain.usecases.FinishRequestUseCase
import com.ibm.internship.beabee.domain.usecases.SubmitFeedbackUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.utils.Constants.Companion.TEL
import com.ibm.internship.beabee.utils.RequestStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MyRequestsViewModel @Inject constructor(
    private val getMyRequestsUseCase: GetMyRequestsUseCase,
    private val getIdUserLoggedInUseCase: GetIdUserLoggedInUseCase,
    private val submitFeedbackUseCase: SubmitFeedbackUseCase,
    private val finishRequestUseCase: FinishRequestUseCase
) : BaseViewModel() {
    private val _listRequests = MutableLiveData<List<GetMyRequestResponse>>()
    val listRequests: LiveData<List<GetMyRequestResponse>> = _listRequests

    private val _isNoRequestFound = MutableLiveData<Boolean>()
    val isNoRequestFound: LiveData<Boolean> = _isNoRequestFound

    private val _phoneDialerUri = MutableLiveData<Uri>()
    val phoneDialerState: LiveData<Uri> = _phoneDialerUri

    private val _ratingDialogState = MutableLiveData<RatingData>()
    val ratingDialogState: LiveData<RatingData> = _ratingDialogState

    private val TAG = this::class.simpleName

    init {
        onGetData()
    }

    fun onGetData() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val getIdResult = getIdUserLoggedInUseCase()) {
                is UseCaseResponse.Success -> fetchData(getIdResult.body)
                is UseCaseResponse.Failure -> showError(getIdResult.error)
            }
            hideLoading()
        }
    }

    fun onMyRequestEvent(event: MyRequestEvent) {
        when (event) {
            is MyRequestEvent.OnContact -> onOpenPhoneDialer(event.phoneNumber)
            is MyRequestEvent.OnSetIsDone -> onSetIsDone(event.ratingData)
            is MyRequestEvent.OnAllDone -> onAllDone(event.requestId, event.userId, event.rating)
            is MyRequestEvent.OnDelete -> onDeleteRequest()
            is MyRequestEvent.OnFailure -> showError(ErrorMessage.GENERAL)
        }
    }

    private fun onOpenPhoneDialer(phoneNumber: String) {
        try {
            _phoneDialerUri.value = Uri.parse("$TEL$phoneNumber")
        } catch (exception: Exception) {
            showError(ErrorMessage.GENERAL)
            BeeLog.e(TAG, "Uri parse failed with exception: $exception")
        }
    }

    private fun onDeleteRequest() = BeeLog.d(TAG, "TODO")

    private fun onSetIsDone(ratingData: RatingData) {
        viewModelScope.launch(Dispatchers.IO) {
            ratingData.userId?.let {
                when (val result = finishRequestUseCase(it, ratingData.requestId)) {
                    is UseCaseResponse.Success -> _ratingDialogState.postValue(ratingData)
                    is UseCaseResponse.Failure -> showError(result.error)
                }
            }
        }
    }

    private fun onAllDone(requestId: String?, userId: String?, rating: Float) {
        if (requestId != null && userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = submitFeedbackUseCase(requestId, userId, rating)) {
                    is UseCaseResponse.Success -> onGetData()
                    is UseCaseResponse.Failure -> showError(result.error)
                }
            }
        }
    }

    private suspend fun fetchData(idUser: String) {
        when (val result = getMyRequestsUseCase(idUser)) {
            is UseCaseResponse.Success -> onGetDataSuccess(result.body)
            is UseCaseResponse.Failure -> onGetDataFail(result.error)
        }
    }

    private fun onGetDataSuccess(listRequests: List<GetMyRequestResponse>) {
        setRequests(listRequests)
        _isNoRequestFound.postValue(listRequests.isEmpty())
        BeeLog.d(TAG, "Get my requests succeeded")
    }

    private fun onGetDataFail(error: ErrorMessage) {
        showError(error)
        BeeLog.e(TAG, "Get my requests failed: $error")
    }

    private fun setRequests(listRequests: List<GetMyRequestResponse>) =
        _listRequests.postValue(listRequests.sortAfterStatus())

    private fun List<GetMyRequestResponse>.sortAfterStatus() =
        sortedWith(
            compareBy(
                { it.status.uppercase() != RequestStatus.APPROVED.toString() },
                { it.status.uppercase() != RequestStatus.PENDING.toString() },
                { it.status }
            )
        )
}