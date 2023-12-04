package com.ibm.internship.beabee.ui.dashboard.askforhelp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentAskForHelpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AskForHelpFragment :
    BaseFragment<FragmentAskForHelpBinding>(FragmentAskForHelpBinding::inflate) {
    private val viewModel: AskForHelpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupObserver()
    }

    private fun setupListener() {
        binding.requestHelpButton.setOnClickListener {
            viewModel.onRequestHelpButtonClick()
        }
    }

    private fun setupObserver() {
        viewModel.navigation.observe(viewLifecycleOwner) { destination ->
            navigate(destination)
        }
    }

    private fun navigate(destination: NavDirections) =
        findNavController().navigate(destination)
}