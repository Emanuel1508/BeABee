package com.ibm.internship.beabee.ui.register

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentUserRegisterBinding
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.isChecked
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.setError
import com.ibm.internship.beabee.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentUserRegisterBinding>(FragmentUserRegisterBinding::inflate) {

    private val viewModel: RegisterViewModel by viewModels()
    private val TAG = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupObservers()
        setupListeners()
        setupChangeFieldsListener()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            with(binding) {
                firstNameState.observe(viewLifecycleOwner) {
                    firstNameTextInputLayout.setError(
                        error = it.error,
                        showIcon = it.isErrorIconVisible
                    )
                }
                lastNameState.observe(viewLifecycleOwner) {
                    lastNameTextInputLayout.setError(
                        error = it.error,
                        showIcon = it.isErrorIconVisible
                    )
                }
                emailState.observe(viewLifecycleOwner) {
                    emailTextInputLayout.setError(
                        error = it.error,
                        showIcon = it.isErrorIconVisible
                    )
                }
                phoneState.observe(viewLifecycleOwner) {
                    phoneTextInputLayout.setError(
                        error = it.error,
                        showIcon = it.isErrorIconVisible
                    )
                }
                passwordState.observe(viewLifecycleOwner) {
                    passwordTextInputLayout.setError(
                        error = it.error,
                        showIcon = it.isErrorIconVisible
                    )
                }
                confirmPasswordState.observe(viewLifecycleOwner) {
                    confirmPasswordTextInputLayout.setError(
                        error = it.error,
                        showIcon = it.isErrorIconVisible
                    )
                }
                checkedTermsAndConditionState.observe(viewLifecycleOwner) {
                    termsAndCondCheckBox.isChecked = it.isChecked()
                }
                checkedPrivacyAndPolicyState.observe(viewLifecycleOwner) {
                    privacyPolicyCheckBox.isChecked = it.isChecked()
                }
                registerButtonState.observe(viewLifecycleOwner) {
                    nextButton.isEnabled = it
                }
                successLiveData.observe(viewLifecycleOwner) {
                    navigateToSuccessScreen()
                }
                errorLiveData.observe(viewLifecycleOwner) {
                    showDialog(it.message)
                }
                loadingLiveData.observe(viewLifecycleOwner) {
                    updateLoginLoadingAnimation(it)
                }
            }
        }
    }

    private fun navigateToSuccessScreen() =
        findNavController().navigate(
            RegisterFragmentDirections
                .actionFragmentRegisterToOnboardingSuccessFragment()
        )


    private fun setupListeners() {
        with(binding) {
            nextButton.setOnClickListener {
                viewModel.onRegisterButtonPressed(
                    email = emailEditText.text.toString(),
                    password = passwordEditText.text.toString()
                )
            }
        }
    }

    private fun setupChangeFieldsListener() {
        with(viewModel) {
            with(binding) {
                firstNameEditText.doAfterTextChanged {
                    onValidateFirstNameEvent(firstNameEditText.text.toString())
                }
                lastNameEditText.doAfterTextChanged {
                    onValidateLastNameEvent(lastNameEditText.text.toString())
                }
                emailEditText.doAfterTextChanged {
                    onValidateEmailEvent(emailEditText.text.toString())
                }
                phoneEditText.doAfterTextChanged {
                    onValidatePhoneEvent(phoneEditText.text.toString())
                }
                passwordEditText.doAfterTextChanged {
                    onValidatePasswordEvent(passwordEditText.text.toString())
                }
                confirmPasswordEditText.doAfterTextChanged {
                    onValidateConfirmPasswordEvent(
                        passwordEditText.text.toString(),
                        confirmPasswordEditText.text.toString()
                    )
                }
                termsAndCondCheckBox.setOnCheckedChangeListener { _, _ ->
                    termsAndCondCheckBox.requestFocusFromTouch()
                    onTermsAndConditionsCheckEvent(termsAndCondCheckBox.isChecked)
                }
                privacyPolicyCheckBox.setOnCheckedChangeListener { _, _ ->
                    privacyPolicyCheckBox.requestFocusFromTouch()
                    onPrivacyAndPolicyCheckEvent(privacyPolicyCheckBox.isChecked)
                }
            }
        }
    }

    private fun showDialog(error: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.sign_up_alert_title),
            description = getString(error.mapToPresentation()),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun updateLoginLoadingAnimation(value: Result.Loading) {
        when (value.shouldShowLoading) {
            true -> showLoadingAnimation()
            false -> hideLoadingAnimation()
        }
    }

    private fun showLoadingAnimation() {
        with(binding) {
            nextButton.hide()
            registerLoadingIndicator.show()
        }
    }

    private fun hideLoadingAnimation() {
        with(binding) {
            registerLoadingIndicator.hide()
            nextButton.show()
        }
    }
}