package com.ibm.internship.beabee.utils

sealed class VisibilityState {
    data object IsVisible : VisibilityState()
    data object NotVisible : VisibilityState()
}