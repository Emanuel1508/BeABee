package com.ibm.internship.beabee.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.ui.main.MainActivity
import com.ibm.internship.beabee.databinding.FragmentUserLoginBinding
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.setError
import com.ibm.internship.beabee.ui.login.UserLoginViewModel.NavDestination.*
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.hideKeyboard
import com.ibm.internship.beabee.utils.setButtonEnabled
import com.ibm.internship.beabee.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserLoginFragment :
    BaseFragment<FragmentUserLoginBinding>(FragmentUserLoginBinding::inflate) {

    private val viewModel: UserLoginViewModel by viewModels()
    private val TAG = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupListeners() {
        with(binding) {
            with(viewModel) {
                emailOrPhoneEditText.doAfterTextChanged {
                    onEvent(
                        LoginEvent.EmailOrPhoneChanged(
                            emailOrPhone = emailOrPhoneEditText.text.toString(),
                        )
                    )
                }
                passwordEditText.doAfterTextChanged {
                    onEvent(
                        LoginEvent.PasswordChanged(
                            password = passwordEditText.text.toString(),
                        )
                    )
                }
                loginButton.setOnClickListener {
                    onEvent(
                        LoginEvent.Submit(
                            email = emailOrPhoneEditText.text.toString(),
                            password = passwordEditText.text.toString()
                        )
                    )
                    hideKeyboard()
                    clearFormFocus()
                }
                forgotPasswordButton.setOnClickListener {
                    onForgotPasswordButtonPress(emailOrPhoneEditText.text.toString())
                }
            }
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            with(binding) {
                emailOrPhoneState.observe(viewLifecycleOwner) {
                    emailOrPhoneTextInputLayout.setError(
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
                isLoginButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
                    loginButton.setButtonEnabled(isEnabled)
                }
                errorLiveData.observe(viewLifecycleOwner) {
                    showDialog(it.message)
                }
                loadingLiveData.observe(viewLifecycleOwner) {
                    updateLoginLoadingAnimation(it)
                }
                successLiveData.observe(viewLifecycleOwner) {
                    Toast.makeText(requireContext(), it.data.toString(), Toast.LENGTH_LONG).show()
                }
                navigation.observe(viewLifecycleOwner) {
                    when (it) {
                        Description -> navigateToDescriptionScreen()
                        Main -> navigateToMainScreen()
                        else -> {}
                    }
                }
            }
        }
    }

    private fun clearFormFocus() {
        with(binding) {
            passwordEditText.clearFocus()
            emailOrPhoneEditText.clearFocus()
        }
    }

    private fun updateLoginLoadingAnimation(value: Result.Loading) {
        when (value.shouldShowLoading) {
            true -> showLoadingAnimation()
            false -> hideLoadingAnimation()
        }
    }

    private fun showLoadingAnimation() {
        with(binding) {
            loginButton.hide()
            loginLoadingIndicator.show()
        }
    }

    private fun hideLoadingAnimation() {
        with(binding) {
            loginLoadingIndicator.hide()
            loginButton.show()
        }
    }

    private fun showDialog(error: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.oops_title),
            description = getString(error.mapToPresentation()),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun navigateToDescriptionScreen() =
        findNavController().navigate(
            UserLoginFragmentDirections
                .actionFragmentLoginToDescription()
        )

    private fun navigateToMainScreen() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}