package com.ibm.internship.beabee.ui.dashboard.myprofile.accountdetails

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentAccountDetailsBinding
import com.ibm.internship.beabee.domain.models.GetUserResponse
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.splitFirstLastName
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.hideKeyboard
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountDetailsFragment :
    BaseFragment<FragmentAccountDetailsBinding>(FragmentAccountDetailsBinding::inflate) {
    private val viewModel: AccountDetailsViewModel by viewModels()
    private val TAG: String = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupObservers()
        setupListeners()
        changeBackButtonBehaviour()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.accountDetailsToolbar
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            backButtonLogic()
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            loadingLiveData.observe(viewLifecycleOwner) {
                updateProfileLoadingAnimation(it)
            }
            errorLiveData.observe(viewLifecycleOwner) {
                showAlertDialog(it.message)
            }
            userDetails.observe(viewLifecycleOwner) { details ->
                insertUserDetailsInEditTexts(details)
            }
            isSaveButtonLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) saveButtonLoading() else saveButtonDisplayed()
            }
            navigation.observe(viewLifecycleOwner) {
                navigateToProfileScreen()
            }
            with(binding) {
                isSaveButtonEnabled.observe(viewLifecycleOwner) {
                    saveChangesButton.isEnabled = it
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            with(viewModel) {
                firstNameEditText.doAfterTextChanged {
                    onEvent(
                        AccountDetailsEvent.NameChanged(
                            firstName = firstNameEditText.text.toString(),
                            lastName = lastNameEditText.text.toString()
                        )
                    )
                }
                lastNameEditText.doAfterTextChanged {
                    onEvent(
                        AccountDetailsEvent.NameChanged(
                            firstName = firstNameEditText.text.toString(),
                            lastName = lastNameEditText.text.toString()
                        )
                    )
                }
                saveChangesButton.setOnClickListener {
                    onEvent(
                        AccountDetailsEvent.SaveChanges(
                            firstName = firstNameEditText.text.toString(),
                            lastName = lastNameEditText.text.toString()
                        )
                    )
                    hideKeyboard()
                    clearFormFocus()
                }
            }
        }
    }

    private fun updateProfileLoadingAnimation(value: Result.Loading) {
        if (value.shouldShowLoading) showLoadingAnimation() else hideLoadingAnimation()
    }

    private fun showLoadingAnimation() {
        with(binding) {
            mainContent.hide()
            centralProgressBar.show()
        }
    }

    private fun hideLoadingAnimation() {
        with(binding) {
            centralProgressBar.hide()
            mainContent.show()
        }
    }

    private fun insertUserDetailsInEditTexts(details: GetUserResponse) {
        with(binding) {
            val name = details.name.splitFirstLastName()
            firstNameEditText.setText(name.first)
            lastNameEditText.setText(name.second)
            emailEditText.setText(details.email)
            phoneNumberEditText.setText(details.phone)
        }
    }

    private fun showAlertDialog(errorMessage: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.update_user_error),
            description = getString(errorMessage.mapToPresentation()),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
            onPositiveButtonClick = { viewModel.getUserAccountDetails() },
            onNegativeButtonClick = { navigateToProfileScreen() }
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun showDismissChangesAlert() {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.dismiss_changes_title),
            description = getString(R.string.dismiss_changes_description),
            positiveOption = getString(R.string.ok_button),
            negativeOption = getString(R.string.cancel_button),
            onPositiveButtonClick = { navigateToProfileScreen() }
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun navigateToProfileScreen() = findNavController().navigate(R.id.myProfile_fragment)

    private fun saveButtonLoading() {
        with(binding) {
            saveChangesButton.hide()
            saveChangesProgressBar.show()
        }
    }

    private fun saveButtonDisplayed() {
        with(binding) {
            saveChangesProgressBar.hide()
            saveChangesButton.show()
        }
    }

    private fun clearFormFocus() {
        with(binding) {
            firstNameEditText.clearFocus()
            lastNameEditText.clearFocus()
        }
    }

    private fun changeBackButtonBehaviour() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = backButtonLogic()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun backButtonLogic() {
        viewModel.isSaveButtonEnabled.value?.let { isEnabled ->
            if (isEnabled) showDismissChangesAlert()
            else findNavController().popBackStack()
        }
    }
}