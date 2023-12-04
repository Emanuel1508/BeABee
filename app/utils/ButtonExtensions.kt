package com.ibm.internship.beabee.utils

import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat

fun Button.enable() {
    this.isEnabled = true
}

fun Button.disable() {
    this.isEnabled = false
}

fun Button.setButtonEnabled(isEnabled: ButtonState) {
    when (isEnabled) {
        ButtonState.Enabled -> this.enable()
        ButtonState.Disabled -> this.disable()
    }
}

fun ImageButton.setIcon(isFirstIconDisplayed: Boolean, firstIcon: Int, secondIcon: Int) {
    val icon =
        if (isFirstIconDisplayed) firstIcon else secondIcon
    setImageDrawable(
        ContextCompat.getDrawable(
            context,
            icon
        )
    )
}