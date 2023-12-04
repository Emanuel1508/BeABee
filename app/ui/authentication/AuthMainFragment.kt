package com.ibm.internship.beabee.ui.authentication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentAuthMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ibm.internship.beabee.ui.authentication.AuthMainViewModel.NavDestination.*

@AndroidEntryPoint
class AuthMainFragment : BaseFragment<FragmentAuthMainBinding>(FragmentAuthMainBinding::inflate) {

    private val viewModel: AuthMainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        with(binding) {
            createAccountButton.setOnClickListener { viewModel.onCreateAccountButtonClick() }
            signInButton.setOnClickListener { viewModel.onLoginButtonClick() }
        }
    }

    private fun setupObservers() {
        val navController = findNavController()
        viewModel.navigation.observe(viewLifecycleOwner) { destination ->
            when (destination) {
                is Register -> navController.navigate(
                    AuthMainFragmentDirections
                        .actionAuthToRegister()
                )
                is Login -> navController.navigate(
                    AuthMainFragmentDirections
                        .actionAuthToLogin()
                )
            }
        }
    }
}
