package com.ibm.internship.beabee.ui.description

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.DescriptionChoiceChipBinding
import com.ibm.internship.beabee.databinding.FragmentUserDescriptionBinding
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.ui.main.MainActivity
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.gone
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.show
import dagger.hilt.android.AndroidEntryPoint
import com.ibm.internship.beabee.utils.ButtonState
import com.ibm.internship.beabee.utils.VisibilityState
import com.ibm.internship.beabee.utils.disable
import com.ibm.internship.beabee.utils.enable

@AndroidEntryPoint
class UserDescriptionFragment :
    BaseFragment<FragmentUserDescriptionBinding>(FragmentUserDescriptionBinding::inflate) {

    private val viewModel: UserDescriptionViewModel by viewModels()
    private val TAG: String = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGreetingMessage()
        setupListeners()
        setupObservers()
        changeBackButtonBehaviour()
    }

    private fun setupListeners() {
        with(binding) {
            with(viewModel) {
                descriptionEditText.doAfterTextChanged {
                    validateText(descriptionEditText.text.toString())
                }
                descriptionAnalyzeButton.setOnClickListener {
                    onAnalyzeButtonClick(descriptionEditText.text.toString())
                }
                descriptionSkipButton.setOnClickListener {
                    buildSkipAlert()
                }
                descriptionNextButton.setOnClickListener {
                    onNextButtonClicked()
                }
            }
        }
    }

    private fun buildSkipAlert() {
        val alertDialog = AlertDialogFragment(
            title = getString(R.string.skip_alert_title),
            description = getString(R.string.skip_alert_text),
            positiveOption = getString(R.string.ok_button),
            negativeOption = getString(R.string.cancel_button).uppercase(),
            onPositiveButtonClick = {
                navigateToMainScreen()
            },
        )
        alertDialog.show(parentFragmentManager, TAG)
    }

    private fun navigateToMainScreen() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupObservers() {
        with(viewModel) {
            with(binding) {
                analyzeButtonState.observe(viewLifecycleOwner) { isEnabled ->
                    when(isEnabled) {
                        is ButtonState.Enabled -> descriptionAnalyzeButton.enable()
                        is ButtonState.Disabled -> descriptionAnalyzeButton.disable()
                    }
                }
                greetingName.observe(viewLifecycleOwner) { message ->
                    descriptionGreetingText.text = message
                }
                tagTextList.observe(viewLifecycleOwner) { tags ->
                    tags?.let {
                        updateChipGroup(tags)
                    }
                }
                errorLiveData.observe(viewLifecycleOwner) {
                    showAlertDialog(it.message)
                }
                loadingLiveData.observe(viewLifecycleOwner) {
                    updateLoadingAnimation(it)
                }
                nextButtonState.observe(viewLifecycleOwner) { isEnabled ->
                    when(isEnabled) {
                        is ButtonState.Enabled -> {
                            descriptionNextButton.show()
                            descriptionNextButton.enable()
                        }
                        is ButtonState.Disabled -> descriptionNextButton.disable()
                    }
                }
                updateUserTagsResult.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is UseCaseResponse.Success -> navigateToMainScreen()
                        is UseCaseResponse.Failure -> showUpdateFailedDialog()
                    }
                }
                isNextLoading.observe(viewLifecycleOwner) { isLoading ->
                    when(isLoading) {
                        is VisibilityState.IsVisible -> showNextButtonLoading()
                        is VisibilityState.NotVisible -> hideNextButtonLoading()
                    }
                }
            }
        }
    }

    private fun setGreetingMessage() {
        viewModel.onSetGreetingText(
            getString(R.string.greeting_description),
            getString(R.string.username_placeholder)
        )
    }

    private fun updateChipGroup(tagList: List<String>) {
        binding.categoriesChipGroup.removeAllViews()
        if (tagList.isNotEmpty()) {
            tagList.forEach { tagText -> initTagChip(tagText) }
        }
    }

    private fun initTagChip(tagText: String) {
        val chip = DescriptionChoiceChipBinding.inflate(
            layoutInflater,
            binding.categoriesChipGroup,
            false
        ).root
        chip.text = tagText
        chip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onChipCheckedChanged(isChecked, tagText)
        }
        binding.categoriesChipGroup.addView(chip)
    }

    private fun showAlertDialog(error: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getStringWithContext(R.string.analyze_alert_title),
            description = getStringWithContext(error.mapToPresentation()),
            positiveOption = getStringWithContext(R.string.retry_button),
            negativeOption = getStringWithContext(R.string.cancel_button),
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun showUpdateFailedDialog() {
        val alertDialogFragment = AlertDialogFragment(
            title = getStringWithContext(R.string.update_tags_failed),
            description = getStringWithContext(ErrorMessage.GENERAL.mapToPresentation()),
            positiveOption = getStringWithContext(R.string.retry_button),
            negativeOption = getStringWithContext(R.string.cancel_button),
            onPositiveButtonClick = { viewModel.onNextButtonClicked() }
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun getStringWithContext(idString: Int) = requireContext().getString(idString)

    private fun updateLoadingAnimation(value: Result.Loading) {
        when (value.shouldShowLoading) {
            true -> showLoadingAnimation()
            false -> hideLoadingAnimation()
        }
    }

    private fun showLoadingAnimation() {
        with(binding) {
            descriptionAnalyzeButton.hide()
            analyzeLoadingIndicator.show()
        }
    }

    private fun hideLoadingAnimation() {
        with(binding) {
            analyzeLoadingIndicator.hide()
            descriptionAnalyzeButton.show()
        }
    }

    private fun showNextButtonLoading() {
        with(binding) {
            nextLoadingIndicator.show()
            descriptionNextButton.gone()
        }
    }

    private fun hideNextButtonLoading() {
        with(binding) {
            nextLoadingIndicator.gone()
            descriptionNextButton.show()
        }
    }

    private fun changeBackButtonBehaviour() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                buildSkipAlert()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}