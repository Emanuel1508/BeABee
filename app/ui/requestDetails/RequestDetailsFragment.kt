package com.ibm.internship.beabee.ui.requestDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentRequestDetailsBinding
import com.ibm.internship.beabee.databinding.RequestDetailsChoiceChipBinding
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.domain.utils.formatDate
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.domain.utils.getInitials
import com.ibm.internship.beabee.utils.Constants.Companion.GOOGLE_MAPS_LINK
import com.ibm.internship.beabee.utils.Constants.Companion.GOOGLE_MAPS_PACKAGE
import com.ibm.internship.beabee.utils.Constants.Companion.MAP_ZOOM
import com.ibm.internship.beabee.utils.Constants.Companion.MARKET_LINK
import com.ibm.internship.beabee.utils.Constants.Companion.TEL
import com.ibm.internship.beabee.utils.gone
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.show
import com.ibm.internship.beabee.utils.hideRefresh
import com.ibm.internship.beabee.utils.setVisibility
import com.ibm.internship.beabee.utils.showRefresh
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestDetailsFragment : BaseFragment<FragmentRequestDetailsBinding>(
    FragmentRequestDetailsBinding::inflate
), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val viewModel: RequestDetailsViewModel by viewModels()
    private val TAG = this::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return super.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupObservers()
        setupListeners()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.requestDetailsToolbar
        getToolbar(toolbar)
        toolbar.setNavigationOnClickListener {
            navigateTo(
                RequestDetailsFragmentDirections
                    .actionRequestDetailsFragmentToRequestsFragment()
            )
        }
    }

    private fun setupMap(requestItem: RequestDetailsUiModel) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(requestItem.coordinates, MAP_ZOOM)
        googleMap.moveCamera(cameraUpdate)
        val markerOptions = MarkerOptions().position(requestItem.coordinates)
        googleMap.addMarker(markerOptions)
    }

    private fun setupObservers() {
        with(viewModel) {
            with(binding) {
                isSecondContainerVisible.observe(viewLifecycleOwner) {
                    firstButtonsContainer.hide()
                    secondButtonsContainer.show()
                }
                statusState.observe(viewLifecycleOwner) { requestStatus ->
                    statusTextView.text = getString(requestStatus.first)
                    statusTextView.setBackgroundResource(requestStatus.second)
                }
                requestDetails.observe(viewLifecycleOwner) { requestItem ->
                    setupViews(requestItem)
                    setupMap(requestItem)
                }
                errorLiveData.observe(viewLifecycleOwner) {
                    scrollViewContainer.gone()
                    showAlertDialog(
                        R.string.oops_title,
                        it.message.mapToPresentation(),
                        ::onGetData
                    )
                }
                phoneNumber.observe(viewLifecycleOwner) {
                    launchDialer(it)
                }
                successLiveData.observe(viewLifecycleOwner) {
                    navigateToRequestsScreen()
                }
                setupLoadingObservers()
                setupMapObserver()
            }
        }
    }

    private fun setupMapObserver() {
        lateinit var mapAppsIntent: Intent
        lateinit var chooserIntent: Intent
        with(viewModel) {
            openMapsState.observe(viewLifecycleOwner) { openMapsState ->
                requestDetails.value?.let {
                    val latitude = it.coordinates.latitude
                    val longitude = it.coordinates.longitude
                    val username = it.requesterName
                    when (openMapsState) {
                        is OpenMapsState.TryOpenMaps -> {
                            mapAppsIntent = buildMapAppIntent(latitude, longitude, username)
                            onOpenMapApp(isAppInstalled(mapAppsIntent))
                        }

                        is OpenMapsState.OpenMaps -> startActivity(mapAppsIntent)

                        is OpenMapsState.TryOpenMapInBrowserOrMarket -> {
                            val marketIntent = buildMarkerIntent()
                            val browserMapIntent =
                                buildBrowserMapIntent(latitude, longitude, username)
                            chooserIntent = buildChooserIntent(marketIntent, browserMapIntent)
                            onOpenMapInBrowserOrMaret(isAppInstalled(chooserIntent))
                        }

                        is OpenMapsState.OpenMapInBrowserOrMarket -> startActivity(chooserIntent)

                        is OpenMapsState.OpenMapsFailed -> showAlertDialog(
                            R.string.map_alert_title,
                            R.string.map_alert_error
                        )
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            with(viewModel) {
                wantToHelpButton.setOnClickListener {
                    onWantToHelpButtonClicked()
                }
                callButton.setOnClickListener {
                    fetchPhoneNumber()
                }
                doneButton.setOnClickListener {
                    onIsDoneButtonClicked()
                }
                sendMessageButton.setOnClickListener {
                    showMessageSentDialog()
                }
                openMapsButton.setOnClickListener {
                    onOpenMaps()
                }
                swipeRefresh.setOnRefreshListener {
                    viewModel.requestDetails.observe(viewLifecycleOwner) { requestItem ->
                        setupViews(requestItem)
                        setupMap(requestItem)
                    }
                    swipeRefresh.hideRefresh()
                }
            }
        }
    }

    private fun setupLoadingObservers() {
        with(viewModel) {
            isIWantHelpLoading.observe(viewLifecycleOwner) {
                toggleIWantToHelpButtonLoading(it)
            }
            isFinishButtonLoading.observe(viewLifecycleOwner) {
                toggleFinishButtonLoading(it)
            }
            loadingLiveData.observe(viewLifecycleOwner) {
                updateLoadingAnimation(it)
            }
        }
    }

    private fun setupViews(requestItem: RequestDetailsUiModel) {
        with(binding) {
            with(requestItem) {
                descriptionTextView.text = description
                detailsLocationTextView.text = location
                notesContentTextView.text = notes
                requesterNameTextView.text = requesterName
                initialsTextView.text = requesterName.getInitials()
                detailsTimeTextView.text = getString(
                    R.string.submitted_details_label,
                    date.formatDate()
                        .ifEmpty { detailsTimeTextView.context.getString(R.string.default_text) }
                )
                updateChipGroup(chips)
            }
        }
    }

    private fun updateLoadingAnimation(value: Result.Loading) =
        toggleLoadingAnimation(value.shouldShowLoading)

    private fun toggleLoadingAnimation(isVisible: Boolean) {
        with(binding) {
            if (isVisible) {
                scrollViewContainer.hide()
                swipeRefresh.showRefresh()
            } else {
                scrollViewContainer.show()
                swipeRefresh.hideRefresh()
            }
        }
    }

    private fun buildMapAppIntent(latitude: Double, longitude: Double, label: String) =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
        )

    private fun buildMarkerIntent() =
        Intent(Intent.ACTION_VIEW, Uri.parse("$MARKET_LINK?id=$GOOGLE_MAPS_PACKAGE"))

    private fun buildBrowserMapIntent(latitude: Double, longitude: Double, label: String) =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("$GOOGLE_MAPS_LINK?q=$latitude,$longitude($label)")
        )

    private fun buildChooserIntent(marketIntent: Intent, browserMapIntent: Intent) =
        Intent.createChooser(marketIntent, getString(R.string.chooser_title)).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(browserMapIntent))
        }

    private fun isAppInstalled(intent: Intent) =
        intent.resolveActivity(requireContext().packageManager) != null

    private fun updateChipGroup(tagList: List<String>) {
        binding.categoriesRequestDetailsChipGroup.removeAllViews()
        tagList.forEach { tagText -> initTagChip(tagText) }
    }

    private fun initTagChip(tagText: String) {
        val chip = RequestDetailsChoiceChipBinding.inflate(
            LayoutInflater.from(context),
            binding.categoriesRequestDetailsChipGroup,
            false
        ).root.apply {
            text = tagText
            isEnabled = false
        }
        binding.categoriesRequestDetailsChipGroup.addView(chip)
    }

    private fun launchDialer(phoneNumber: String) {
        val dial = Uri.parse("$TEL$phoneNumber")
        val dialIntent = Intent(Intent.ACTION_DIAL, dial)
        startActivity(dialIntent)
    }

    private fun showMessageSentDialog() {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.message_sent_title),
            description = getString(R.string.message_sent_description),
            positiveOption = getString(R.string.ok_button)
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun toggleIWantToHelpButtonLoading(isLoading: Boolean) {
        with(binding) {
            wantToHelpButton.setVisibility(!isLoading)
            wantToHelpProgressBar.setVisibility(isLoading)
        }
    }

    private fun toggleFinishButtonLoading(isLoading: Boolean) {
        with(binding) {
            doneButton.setVisibility(!isLoading)
            doneProgressBar.setVisibility(isLoading)
        }
    }

    private fun showAlertDialog(
        titleStringResource: Int,
        descriptionStringResource: Int,
        onRetry: () -> Unit = {}
    ) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(titleStringResource),
            description = getString(descriptionStringResource),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
            onPositiveButtonClick = { onRetry() }
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun navigateToRequestsScreen() = findNavController().navigate(R.id.requests_fragment)
}