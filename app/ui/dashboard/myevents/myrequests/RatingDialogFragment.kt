package com.ibm.internship.beabee.ui.dashboard.myevents.myrequests

import android.app.Dialog
import android.os.Bundle
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.databinding.LayoutRatingBinding
import com.ibm.internship.beabee.utils.Constants.Companion.DEFAULT_RATING
import java.io.Serializable

class RatingDialogFragment : DialogFragment() {
    private lateinit var binding: LayoutRatingBinding

    companion object {
        private const val USERNAME: String = "USERNAME"
        private const val NAME_INITIALS: String = "NAME_INITIALS"
        private const val CALLBACK_ON_GO_BACK: String = "CALLBACK_ON_GO_BACK"
        private const val CALLBACK_ON_ALL_DONE: String = "CALLBACK_ON_ALL_DONE"

        fun newInstance(
            userName: String?,
            userNameInitials: String?,
            onGoBackClick: () -> Unit,
            onAllDoneClick: (rating: Float) -> Unit
        ): RatingDialogFragment {
            val fragment = RatingDialogFragment()
            val bundle = Bundle().apply {
                putString(USERNAME, userName)
                putString(NAME_INITIALS, userNameInitials)
                putSerializable(CALLBACK_ON_GO_BACK, onGoBackClick as Serializable)
                putSerializable(CALLBACK_ON_ALL_DONE, onAllDoneClick as Serializable)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = LayoutRatingBinding.inflate(layoutInflater)
        isCancelable = false
        setupListeners()
        setupViews()
        return setupDialog()?.create() ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setupDialog() =
        activity?.let { MaterialAlertDialogBuilder(it, R.style.AlertDialog).setView(binding.root) }

    private fun setupListeners() {
        var rating: Float = DEFAULT_RATING
        with(binding) {
            val onAllDone = getAllDoneCallBack()
            val onGoBack = getGoBackCallback()
            allDoneButton.setOnClickListener {
                onAllDone?.invoke(rating)
                dialog?.dismiss()
            }
            goBackButton.setOnClickListener {
                onGoBack?.invoke()
                dialog?.dismiss()
            }
            userRatingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _, ratingValue, _ ->
                    rating = ratingValue
                }
        }
    }

    private fun setupViews() {
        binding.myRequestAvatar.initialsTextView.text = arguments?.getString(NAME_INITIALS)
        binding.userNameTextView.text = arguments?.getString(USERNAME)
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun getAllDoneCallBack() =
        arguments?.getSerializable(CALLBACK_ON_ALL_DONE) as? (Float) -> Unit

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun getGoBackCallback() = arguments?.getSerializable(CALLBACK_ON_GO_BACK) as? () -> Unit
}