package com.ibm.internship.beabee.utils

data class CheckboxState(
    var isChecked: Boolean = false
)

fun CheckboxState.isChecked() = this.isChecked