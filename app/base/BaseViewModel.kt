package com.ibm.internship.beabee.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.domain.utils.ErrorMessage

abstract class BaseViewModel : ViewModel() {
    private val _errorLiveData: MutableLiveData<Result.Error> = MutableLiveData()
    val errorLiveData: LiveData<Result.Error> get() = _errorLiveData

    private val _loadingLiveData: MutableLiveData<Result.Loading> = MutableLiveData()
    val loadingLiveData: LiveData<Result.Loading> get() = _loadingLiveData

    private val _successLiveData: MutableLiveData<Result.Success<Any>> = MutableLiveData()
    val successLiveData: LiveData<Result.Success<Any>> get() = _successLiveData

    // Methods to update LiveData
    protected fun showError(message: ErrorMessage) = _errorLiveData.postValue(Result.Error(message))

    protected fun showLoading() = _loadingLiveData.postValue(Result.Loading(true))

    protected fun hideLoading() = _loadingLiveData.postValue(Result.Loading(false))

    protected fun <T : Any> showSuccess(result: T) =
        _successLiveData.postValue(Result.Success(result))
}