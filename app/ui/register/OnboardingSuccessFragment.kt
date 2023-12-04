package com.ibm.internship.beabee.ui.register

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentSuccesScreenBinding
import com.ibm.internship.beabee.utils.Constants

class OnboardingSuccessFragment :
    BaseFragment<FragmentSuccesScreenBinding>(FragmentSuccesScreenBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTimer()
    }

    private fun startTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            switchScreens()
        }, Constants.TIME_DELAY)
    }

    private fun switchScreens() =
        findNavController().navigate(
            OnboardingSuccessFragmentDirections.actionOnboardingSuccessFragmentToFragmentLogin()
        )
}
