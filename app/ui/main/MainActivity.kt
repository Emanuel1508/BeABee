package com.ibm.internship.beabee.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseActivity
import com.ibm.internship.beabee.databinding.ActivityMainBinding
import com.ibm.internship.beabee.ui.notifications.NotificationsViewModel
import com.ibm.internship.beabee.utils.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar.mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.mainContent_fragmentContainer)
                    as NavHostFragment).navController
        binding.bottomNav.setupWithNavController(navController)
        setupNavBarListeners(navController)
        setupToolbarListeners(navController)
        setupNotificationsIcon()
        setBottomNavBar(navController)
    }

    private fun setupToolbarListeners(navController: NavController) {
        with(binding) {
            toolbar.notificationIcon.setOnClickListener {
                navController.navigate(R.id.notifications_fragment)
                viewModel.setIsAnyNotificationNew(false)
            }
            toolbar.messageIcon.setOnClickListener {
                navController.navigate(R.id.action_to_messagingFragment)
            }
        }
    }

    private fun setupNotificationsIcon() {
        viewModel.isAnyNotificationNew.observe(this) { notificationState ->
            val icon = getIcon(notificationState.resource)
            binding.toolbar.notificationIcon.setImageDrawable(icon)
        }
    }

    private fun getIcon(resource: Int) = ContextCompat.getDrawable(this, resource)

    private fun setupNavBarListeners(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            setupBottomNavVisibility(destination.id)
            setupMainToolbarVisibility(destination.id)
            setupToolbarTitle(destination.id)
            setupBottomNavSelected(destination.id)
        }
    }

    private fun setupBottomNavVisibility(destination: Int) =
        binding.bottomNav.setVisibility(getMainDestinations().contains(destination))

    private fun setupMainToolbarVisibility(destination: Int) {
        binding.toolbar.mainToolbar.setVisibility(getMainDestinations().contains(destination))
    }

    private fun setupBottomNavSelected(destination: Int) {
        if (destination in getMainDestinations()) {
            with(binding.bottomNav.menu) {
                if (getNoBottomNavSelectionDestinations().contains(destination)) {
                    setGroupCheckable(0, false, true)
                } else {
                    findItem(destination).isChecked = true
                    setGroupCheckable(0, true, true)
                }
            }
        }
    }

    private fun setBottomNavBar(navController: NavController) {
        with(binding.bottomNav) {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.requests_fragment -> navController.navigate(R.id.requests_fragment)
                    R.id.askForHelp_fragment -> navController.navigate(R.id.askForHelp_fragment)
                    R.id.myEvents_fragment -> navController.navigate(R.id.myEvents_fragment)
                    R.id.myProfile_fragment -> navController.navigate(R.id.myProfile_fragment)
                }
                true
            }
        }
    }

    private fun getNoBottomNavSelectionDestinations() =
        listOf(R.id.notifications_fragment, R.id.messaging_fragment)

    private fun getMainDestinations() =
        listOf(
            R.id.requests_fragment,
            R.id.myEvents_fragment,
            R.id.askForHelp_fragment,
            R.id.myProfile_fragment,
            R.id.messaging_fragment,
            R.id.notifications_fragment
        )

    private fun setupToolbarTitle(destinationId: Int) {
        val title = when (destinationId) {
            R.id.requests_fragment -> getString(R.string.requests_title)
            R.id.askForHelp_fragment -> getString(R.string.ask_for_help_title)
            R.id.myEvents_fragment -> getString(R.string.my_events_title)
            R.id.myProfile_fragment -> getString(R.string.my_profile_title)
            R.id.messaging_fragment -> getString(R.string.messaging_title)
            R.id.notifications_fragment -> getString(R.string.notifications_title)
            else -> return
        }
        binding.toolbar.toolbarTitle.text = title
    }
}