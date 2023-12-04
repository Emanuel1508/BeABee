package com.ibm.internship.beabee.ui.dashboard.myevents.myrequests

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentMyRequestsBinding
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.Constants.Companion.START_POSITION
import com.ibm.internship.beabee.utils.hideRefresh
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRequestsFragment :
    BaseFragment<FragmentMyRequestsBinding>(FragmentMyRequestsBinding::inflate) {
    private val viewModel: MyRequestsViewModel by viewModels()
    private lateinit var myRequestsAdapter: MyRequestsAdapter
    private val TAG = this::class.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMyRequestAdapter()
        setupObservers()
        setupListener()
    }

    private fun setupMyRequestAdapter() {
        with(viewModel) {
            myRequestsAdapter = MyRequestsAdapter(::onMyRequestEvent)
            val recyclerView: RecyclerView = binding.requestsRecyclerView
            recyclerView.adapter = myRequestsAdapter
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            with(binding) {
                listRequests.observe(viewLifecycleOwner) { listRequests ->
                    myRequestsAdapter.setRequests(listRequests)
                }
                loadingLiveData.observe(viewLifecycleOwner) {
                    swipeRefresh.isRefreshing = it.shouldShowLoading
                }
                errorLiveData.observe(viewLifecycleOwner) { error ->
                    showAlertDialog(error.message)
                }
                isNoRequestFound.observe(viewLifecycleOwner) { isVisible ->
                    noRequestsTextView.setVisibility(isVisible)
                }
                phoneDialerState.observe(viewLifecycleOwner) { uri ->
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    startActivity(intent)
                }
                ratingDialogState.observe(viewLifecycleOwner) { ratingData ->
                    showRatingDialog(ratingData)
                }
            }
        }
    }

    private fun setupListener() {
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                viewModel.listRequests.observe(viewLifecycleOwner) { listRequests ->
                    myRequestsAdapter.notifyItemRangeChanged(START_POSITION, listRequests.size)
                }
                swipeRefresh.hideRefresh()
            }
        }
    }

    private fun showAlertDialog(error: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.oops_title),
            description = getString(error.mapToPresentation()),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
            onPositiveButtonClick = viewModel::onGetData
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun showRatingDialog(ratingData: RatingData) {
        with(viewModel) {
            with(ratingData) {
                val myFragment = RatingDialogFragment.newInstance(
                    userName = userName,
                    userNameInitials = userNameInitials,
                    onGoBackClick = ::onGetData,
                    onAllDoneClick = { rating ->
                        onMyRequestEvent(MyRequestEvent.OnAllDone(requestId, userId, rating))
                    },
                )
                myFragment.show(parentFragmentManager, TAG)
            }
        }
    }
}