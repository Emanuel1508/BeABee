package com.ibm.internship.beabee.ui.dashboard.askforhelp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentAskForHelpStepTwoBinding
import com.ibm.internship.beabee.utils.ButtonState
import com.ibm.internship.beabee.utils.disable
import com.ibm.internship.beabee.utils.enable
import com.ibm.internship.beabee.utils.gone
import com.ibm.internship.beabee.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AskForHelpStepTwoFragment :
    BaseFragment<FragmentAskForHelpStepTwoBinding>(FragmentAskForHelpStepTwoBinding::inflate) {

    private val viewModel: AskForHelpStepTwoViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onLocationPermissionChange(granted = true, wasDenied = !isGranted)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        when {
            isLocationPermissionGranted() -> viewModel.onLocationPermissionChange(granted = true)

            !shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) && !isLocationPermissionGranted() -> {
                viewModel.onLocationPermissionChange(granted = false)
            }

            else -> viewModel.onLocationPermissionChange(granted = false, wasDenied = true)
        }
    }

    private fun setupListeners() {
        with(binding) {
            with(viewModel) {
                locateMeButton.setOnClickListener {
                    onLocateMeButtonClicked()
                }
                nextButton.setOnClickListener {
                    onNextButtonClicked()
                }
                previousButton.setOnClickListener {
                    onPreviousButtonClicked()
                }
                locationEditText.doAfterTextChanged {
                    enableNextButton(locationEditText.text.toString())
                }
            }
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            with(binding) {
                locateMeButtonState.observe(viewLifecycleOwner) { isEnabled ->
                    when (isEnabled) {
                        is ButtonState.Enabled -> {
                            locateMeButton.enable()
                            locationAllowTipTextView.show()
                            locationDenyTipTextView.gone()
                        }

                        is ButtonState.Disabled -> {
                            locateMeButton.disable()
                            locationAllowTipTextView.gone()
                            locationDenyTipTextView.show()
                        }
                    }
                }

                nextButtonState.observe(viewLifecycleOwner) { isEnabled ->
                    when (isEnabled) {
                        is ButtonState.Enabled -> nextButton.enable()
                        is ButtonState.Disabled -> nextButton.disable()
                    }
                }

                location.observe(viewLifecycleOwner) { location ->
                    locationEditText.setText(location)
                    enableNextButton(location)
                }

                locationPermissionState.observe(viewLifecycleOwner) { state ->
                    when (state) {
                        is AskForHelpStepTwoViewModel.LocationPermissionState.CheckPermission ->
                            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                    }
                }

                navigation.observe(viewLifecycleOwner) { navigationDestination ->
                    navigate(navigationDestination)
                }
            }
        }
    }


    private fun isLocationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(), ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun navigate(destination: NavDirections) = findNavController().navigate(destination)
}