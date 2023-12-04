package com.ibm.internship.beabee.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.models.GetNotificationResponse
import com.ibm.internship.beabee.domain.usecases.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : BaseViewModel() {

    private val _notifications = MutableLiveData<List<GetNotificationResponse>>()
    val notifications: LiveData<List<GetNotificationResponse>> = _notifications

    private val _isAnyNotificationNew = MutableLiveData<NotificationsState>()
    val isAnyNotificationNew: LiveData<NotificationsState> = _isAnyNotificationNew

    private companion object {
        val TAG: String = this::class.java.simpleName
    }

    init {
        onGetData()
    }

    fun onGetData() {
        BeeLog.d(TAG, "Get data")
        viewModelScope.launch {
            showLoading()
            val result = getNotificationsUseCase("userId")
            _notifications.value = result
            checkForNewNotifications()
            hideLoading()
        }
    }

    fun setIsAnyNotificationNew(isNew: Boolean) {
        if (isNew)
            _isAnyNotificationNew.value = NotificationsState.NewNotifications
        else
            _isAnyNotificationNew.value = NotificationsState.OldNotifications
    }

    private fun checkForNewNotifications() {
        val result = _notifications.value?.filter { it.isRead }
        setIsAnyNotificationNew(!result.isNullOrEmpty())
    }
}