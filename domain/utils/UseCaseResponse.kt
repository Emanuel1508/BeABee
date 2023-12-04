package com.ibm.internship.beabee.domain.utils

sealed class UseCaseResponse<out T> {
    data class Success<out T>(val body: T) : UseCaseResponse<T>()

    data class Failure(val error: ErrorMessage) : UseCaseResponse<Nothing>()
}
