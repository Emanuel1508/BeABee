package com.ibm.internship.beabee.ui.dashboard.askforhelp

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.DescriptionChoiceChipBinding
import com.ibm.internship.beabee.databinding.FragmentAskForHelpStepOneBinding
import com.ibm.internship.beabee.domain.models.Result
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.mapToPresentation
import dagger.hilt.android.AndroidEntryPoint
import com.ibm.internship.beabee.utils.ButtonState
import com.ibm.internship.beabee.utils.disable
import com.ibm.internship.beabee.utils.enable
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.show

@AndroidEntryPoint
class AskForHelpStepOneFragment :
    BaseFragment<FragmentAskForHelpStepOneBinding>(FragmentAskForHelpStepOneBinding::inflate) {

    private val viewModel: AskForHelpStepOneViewModel by viewModels()
    private val TAG: String = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        with(binding) {
            with(viewModel) {
                analyzeButton.setOnClickListener {
                    onAnalyzeButtonClick(askForHelpEditText.text.toString())
                }
                askForHelpEditText.addTextChangedListener {
                    onDescriptionTextChange(askForHelpEditText.text.toString())
                }
                nextButton.setOnClickListener {
                    onNextButtonClick()
                }
            }
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            with(binding) {
                analyzeButtonState.observe(viewLifecycleOwner) { enableState ->
                    when (enableState) {
                        is ButtonState.Enabled -> analyzeButton.enable()
                        is ButtonState.Disabled -> analyzeButton.disable()
                    }
                }
                chipTags.observe(viewLifecycleOwner) { tagList ->
                    tagList?.let {
                        updateChipGroup(tagList)
                    }
                }
                nextButtonState.observe(viewLifecycleOwner) { enableState ->
                    when (enableState) {
                        is ButtonState.Enabled -> {
                            nextButton.show()
                            nextButton.enable()
                        }
                        is ButtonState.Disabled -> nextButton.hide()
                    }
                }
                navigation.observe(viewLifecycleOwner) { navigationDestination ->
                    navigate(navigationDestination)
                }
                errorLiveData.observe(viewLifecycleOwner) { error ->
                    showAlertDialog(error.message)
                }
                loadingLiveData.observe(viewLifecycleOwner) { isAnimating ->
                    updateLoadingAnimation(isAnimating)
                }
            }
        }
    }

    private fun updateChipGroup(tagList: List<String>) {
        with(binding) {
            categoriesChipGroup.removeAllViews()
            tagList.forEach { tagText -> initTagChip(tagText) }
        }
    }

    private fun initTagChip(tagText: String) {
        val chip = DescriptionChoiceChipBinding.inflate(
            layoutInflater,
            binding.categoriesChipGroup,
            false
        ).root.apply {
            text = tagText
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.onCheckedStateChanged(tagText, isChecked)
            }
        }
        binding.categoriesChipGroup.addView(chip)
    }

    private fun showAlertDialog(error: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.analyze_alert_title),
            description = getString(error.mapToPresentation()),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun updateLoadingAnimation(value: Result.Loading) {
        when (value.shouldShowLoading) {
            true -> showLoadingAnimation()
            false -> hideLoadingAnimation()
        }
    }

    private fun showLoadingAnimation() {
        with(binding) {
            analyzeButton.visibility = View.INVISIBLE
            analyzeLoadingIndicator.visibility = View.VISIBLE
        }
    }

    private fun hideLoadingAnimation() {
        with(binding) {
            analyzeLoadingIndicator.visibility = View.INVISIBLE
            analyzeButton.visibility = View.VISIBLE
        }
    }

    private fun navigate(destination: NavDirections) {
        findNavController().navigate(destination)
    }
}
