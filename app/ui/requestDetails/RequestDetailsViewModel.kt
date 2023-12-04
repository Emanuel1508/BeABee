package com.ibm.internship.beabee.ui.requestDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.utils.StatusMessage
import com.ibm.internship.beabee.utils.mapToPresentation
import com.google.android.gms.maps.model.LatLng
import com.ibm.beabee.data.utils.RequestId
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.domain.models.RequestDetailsResponse
import com.ibm.internship.beabee.domain.usecases.AcceptRequestUseCase
import com.ibm.internship.beabee.domain.usecases.FinishRequestUseCase
import com.ibm.internship.beabee.domain.usecases.GetIdUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.RequestDetailsUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val requestDetailsUseCase: RequestDetailsUseCase,
    private val acceptRequestUseCase: AcceptRequestUseCase,
    private val finishRequestUseCase: FinishRequestUseCase,
    private val getIdUserLoggedInUseCase: GetIdUserLoggedInUseCase,
    @RequestId
    private val requestId: String?
) : BaseViewModel() {

    private val _isSecondContainerVisible = MutableLiveData<Boolean>()
    val isSecondContainerVisible: LiveData<Boolean> get() = _isSecondContainerVisible

    private val _statusState = MutableLiveData(StatusMessage.PENDING.mapToPresentation())
    val statusState: MutableLiveData<Pair<Int, Int>> get() = _statusState

    private val _requestDetails = MutableLiveData<RequestDetailsUiModel>()
    val requestDetails: LiveData<RequestDetailsUiModel> get() = _requestDetails

    private val _phoneNumber: MutableLiveData<String> = MutableLiveData()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private var _isIWantHelpLoading = MutableLiveData<Boolean>()
    val isIWantHelpLoading: LiveData<Boolean> get() = _isIWantHelpLoading

    private var _isFinishButtonLoading = MutableLiveData<Boolean>()
    val isFinishButtonLoading: LiveData<Boolean> get() = _isFinishButtonLoading

    private val _openMapsState = MutableLiveData<OpenMapsState>()
    val openMapsState: LiveData<OpenMapsState> = _openMapsState

    companion object {
        private val TAG: String = this::class.java.simpleName
    }

    init {
        onGetData()
    }

    fun onWantToHelpButtonClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _isIWantHelpLoading.postValue(true)
            when (val loggedInUserId = getIdUserLoggedInUseCase()) {
                is UseCaseResponse.Success -> requestId?.let { acceptRequest(it, loggedInUserId.body) }
                is UseCaseResponse.Failure -> onFail(loggedInUserId.error)
            }
            _isIWantHelpLoading.postValue(false)
        }
    }

    private suspend fun acceptRequest(requestId: String, userId: String) {
        when (val response = acceptRequestUseCase(requestId, userId)) {
            is UseCaseResponse.Success -> onAcceptRequestSuccess()
            is UseCaseResponse.Failure -> onFail(response.error)
        }
    }

    private fun onAcceptRequestSuccess() {
        _isSecondContainerVisible.postValue(true)
        _statusState.postValue(StatusMessage.PROGRESS.mapToPresentation())
    }

    fun onIsDoneButtonClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _isFinishButtonLoading.postValue(true)
            when (val loggedInUserId = getIdUserLoggedInUseCase()) {
                is UseCaseResponse.Success -> requestId?.let { finishRequest(it, loggedInUserId.body) }
                is UseCaseResponse.Failure -> onFail(loggedInUserId.error)
            }
            _isFinishButtonLoading.postValue(false)
        }
    }

    private suspend fun finishRequest(requestId: String, userId: String) {
        when (val response = finishRequestUseCase(userId, requestId)) {
            is UseCaseResponse.Success -> showSuccess(true)
            is UseCaseResponse.Failure -> onFail(response.error)
        }
    }

    fun fetchPhoneNumber() {
        _requestDetails.value?.requesterPhone.let { fetchedNumber ->
            _phoneNumber.postValue(fetchedNumber)
        }
    }

    fun onGetData() =
        requestId?.let { fetchRequestDetails(requestId) } ?: showError(ErrorMessage.GENERAL)

    fun onOpenMaps() {
        _openMapsState.value = OpenMapsState.TryOpenMaps
    }

    fun onOpenMapApp(isMapAppInstalled: Boolean) {
        if (isMapAppInstalled) {
            _openMapsState.value = OpenMapsState.OpenMaps
        } else {
            _openMapsState.value = OpenMapsState.TryOpenMapInBrowserOrMarket
        }
        BeeLog.d(TAG, _openMapsState.value.toString())
    }

    fun onOpenMapInBrowserOrMaret(isChooserAppInstalled: Boolean) {
        if (isChooserAppInstalled) {
            _openMapsState.value = OpenMapsState.OpenMapInBrowserOrMarket
        } else {
            _openMapsState.value = OpenMapsState.OpenMapsFailed
        }
        BeeLog.d(TAG, _openMapsState.value.toString())
    }

    private fun fetchRequestDetails(requestId: String) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = requestDetailsUseCase.getRequestDetails(requestId)) {
                is UseCaseResponse.Success -> onGetDataSuccess(result.body)
                is UseCaseResponse.Failure -> onFail(result.error)
            }
        }
    }

    private fun onGetDataSuccess(requestItem: RequestDetailsResponse) {
        hideLoading()
        _requestDetails.postValue(
            RequestDetailsUiModel(
                requestItem.id,
                requestItem.date,
                requestItem.description,
                requestItem.location,
                requestItem.notes,
                requestItem.requesterName,
                requestItem.requesterPhone,
                requestItem.chips,
                requestItem.status,
                LatLng(requestItem.lat.toDouble(), requestItem.long.toDouble())
            )
        )
    }

    private fun onFail(error: ErrorMessage) {
        hideLoading()
        showError(error)
    }
}