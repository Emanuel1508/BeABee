package com.ibm.internship.beabee.ui.dashboard.myprofile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.ChipTagRequestBinding
import com.ibm.internship.beabee.databinding.FragmentMyProfileBinding
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.ui.main.LoginActivity
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.hideRefresh
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.setVisibility
import com.ibm.internship.beabee.utils.show
import com.ibm.internship.beabee.utils.showRefresh
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileFragment :
    BaseFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {

    private val viewModel: MyProfileViewModel by viewModels()
    private val TAG = this.javaClass::class.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        with(binding) {
            with(viewModel) {
                logoutButton.setOnClickListener {
                    onLogOut()
                }
                deactivateAccountButton.setOnClickListener {
                    onDeactivateAccount()
                }
                manageAccountButton.setOnClickListener {
                    BeeLog.d(TAG, "Open manage account page")
                }
                accountDetailsCard.setOnClickListener {
                    onAccountDetailsClicked()
                }
                seeChipsText.setOnClickListener {
                    onUpdateCurrentListTags()
                }
                swipeRefresh.setOnRefreshListener {
                    viewModel.profileData.observe(viewLifecycleOwner) {
                        refreshTags(it.tags)
                    }
                    swipeRefresh.hideRefresh()
                }
            }
        }
    }

    private fun setupObservers() {
        with(binding) {
            with(viewModel) {
                profileData.observe(viewLifecycleOwner) { data ->
                    setupProfileViews(data)
                }
                listTagsState.observe(viewLifecycleOwner) {
                    seeChipsText.text = getString(it.stringResource)
                }
                errorLiveData.observe(viewLifecycleOwner) {
                    showDialog(it.message)
                }
                loadingLiveData.observe(viewLifecycleOwner) {
                    updateProfileLoadingAnimation(it)
                }
                isSeeMoreVisible.observe(viewLifecycleOwner) {
                    seeChipsText.setVisibility(it)
                }
                navigation.observe(viewLifecycleOwner) { destination ->
                    when (destination) {
                        is ProfileDestination.LoginScreen -> navigateToLoginScreen()
                        is ProfileDestination.ManageAccount -> BeeLog.d(TAG, "TODO")
                        is ProfileDestination.AccountDetails -> navigateToAccountDetails()
                    }
                }
            }
        }
    }

    private fun setupProfileViews(data: ProfileData) {
        with(binding) {
            data.apply {
                nameTextView.text = fullName
                initialsTextView.text = nameInitials
                numberHelpedTextView.text = numberPeopleHelped.toString()
                setRating(rating)
                refreshTags(tags)
            }
        }
    }

    private fun updateProfileLoadingAnimation(value: Result.Loading) {
        when (value.shouldShowLoading) {
            true -> showLoadingAnimation()
            false -> hideLoadingAnimation()
        }
    }

    private fun showLoadingAnimation() {
        with(binding) {
            profilePageBody.hide()
            swipeRefresh.showRefresh()
        }
    }

    private fun hideLoadingAnimation() {
        with(binding) {
            profilePageBody.show()
            swipeRefresh.hideRefresh()
        }
    }

    private fun setRating(rating: Float) {
        with(binding) {
            numberRatingTextView.text = rating.toString()
            userRatingBar.rating = rating
        }
    }

    private fun refreshTags(listTags: List<String>?) {
        with(binding) {
            chipsProfileContainer.removeAllViews()
            listTags?.forEach { tag ->
                val chip = createChip(tag)
                chipsProfileContainer.addView(chip)
            }
        }
    }

    private fun createChip(text: String): Chip {
        val chip = ChipTagRequestBinding.inflate(
            layoutInflater,
            binding.chipsProfileContainer,
            false
        ).root
        chip.text = text
        return chip
    }

    private fun showDialog(error: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.oops_title),
            description = getString(error.mapToPresentation()),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
            onPositiveButtonClick = viewModel::onGetData
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun navigateToAccountDetails() =
        findNavController().navigate(R.id.accountDetailsFragment)


    private fun navigateToLoginScreen() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}