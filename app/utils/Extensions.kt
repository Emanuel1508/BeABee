package com.ibm.internship.beabee.utils

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.domain.utils.ValidationMessage

fun TextInputLayout.showErrorIcon(showIcon: Boolean) {
    if (!showIcon) {
        errorIconDrawable = null
    } else {
        setErrorIconDrawable(R.drawable.ic_error)
    }
}

fun TextInputLayout.setError(error: ValidationMessage, showIcon: Boolean) {
    this.error =
        if (error == ValidationMessage.ERROR_NOT_FOUND || error == ValidationMessage.NOT_CHECKED) {
            null
        } else {
            context.getString(error.mapToPresentation())
        }
    this.showErrorIcon(showIcon)
}

fun Fragment.hideKeyboard() = view?.let { activity?.hideKeyboard(it) }

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun SearchView.refresh() {
    this.setQuery("", false)
    this.clearFocus()
}

fun SwipeRefreshLayout.hideRefresh() {
    this.isRefreshing = false
}

fun SwipeRefreshLayout.showRefresh() {
    this.isRefreshing = true
}
