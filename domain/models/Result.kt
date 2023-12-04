package com.ibm.internship.beabee.domain.models

import com.ibm.internship.beabee.domain.utils.ErrorMessage

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: ErrorMessage) : Result<Nothing>()
    data class Loading(val shouldShowLoading: Boolean) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[message=$message]"
            is Loading-> "Loading"
        }
    }
}