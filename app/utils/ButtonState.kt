package com.ibm.internship.beabee.utils

sealed class ButtonState {
    data object Enabled : ButtonState()
    data object Disabled : ButtonState()
}