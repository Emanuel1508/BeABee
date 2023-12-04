package com.ibm.internship.beabee.utils

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.utils.Constants.Companion.EMPTY_STRING

class AlertDialogFragment(
    private val title: String = EMPTY_STRING,
    private val description: String = EMPTY_STRING,
    private val positiveOption: String = EMPTY_STRING,
    private val negativeOption: String = EMPTY_STRING,
    private val onPositiveButtonClick: () -> Unit = {},
    private val onNegativeButtonClick: () -> Unit = {},
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton(
                positiveOption
            ) { _, _ -> onPositiveButtonClick() }
            .setNegativeButton(
                negativeOption
            ) { _, _ -> onNegativeButtonClick() }
            .create()
}