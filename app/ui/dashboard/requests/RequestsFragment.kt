package com.ibm.internship.beabee.ui.dashboard.requests

import android.content.ContentValues.TAG
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentRequestsBinding
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.utils.AlertDialogFragment
import com.ibm.internship.beabee.utils.hide
import com.ibm.internship.beabee.utils.mapToPresentation
import com.ibm.internship.beabee.utils.show
import com.ibm.internship.beabee.ui.dashboard.requests.RequestsViewModel.ListStatus
import dagger.hilt.android.AndroidEntryPoint
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import com.ibm.internship.beabee.utils.hideKeyboard
import com.ibm.internship.beabee.utils.hideRefresh
import com.ibm.internship.beabee.utils.refresh

@AndroidEntryPoint
class RequestsFragment : BaseFragment<FragmentRequestsBinding>(FragmentRequestsBinding::inflate) {
    private val viewModel: RequestsViewModel by viewModels()
    private lateinit var adapter: RequestsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoading()
        setupRequestCardsAdapter()
        setupObservers()
        setupListeners()
    }

    private fun setupRequestCardsAdapter() {
        adapter = RequestsAdapter { requestId ->
            viewModel.onHelpButtonClick(requestId)
        }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        viewModel.cards.observe(viewLifecycleOwner) { cards ->
            adapter.setCards(cards)
        }
    }

    private fun setupLoading() {
        viewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading.shouldShowLoading
        }
    }

    private fun showErrorDialog(error: ErrorMessage) {
        val alertDialogFragment = AlertDialogFragment(
            title = getString(R.string.oops_title),
            description = getString(error.mapToPresentation()),
            positiveOption = getString(R.string.retry_button),
            negativeOption = getString(R.string.cancel_button),
            onPositiveButtonClick = { viewModel.userClickedRetry() }
        )
        alertDialogFragment.show(parentFragmentManager, TAG)
    }

    private fun setupObservers() {
        with(viewModel) {
            navigation.observe(viewLifecycleOwner) { navDestination ->
                val action =
                    RequestsFragmentDirections.actionRequestsFragmentToRequestDetailsFragment(
                        (navDestination as RequestsViewModel.NavDestination.RequestDetails).requestId
                    )
                navigateTo(action)
            }
            errorLiveData.observe(viewLifecycleOwner) {
                showErrorDialog(it.message)
            }
            cardListStatus.observe(viewLifecycleOwner) { isPopulated ->
                with(binding) {
                    when (isPopulated) {
                        is ListStatus.IsPopulated -> requestsNotFoundTextView.hide()
                        is ListStatus.NotPopulated -> requestsNotFoundTextView.show()
                    }
                }
            }
            searchBarRefreshState.observe(viewLifecycleOwner) {
                binding.requestsSearchBarSearchView.refresh()
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            requestsSearchBarSearchView.setOnKeyListener { _, id, _ ->
                if (isDonePressed(id)) {
                    hideKeyboard()
                }
                true
            }
            requestsConstraintLayout.setOnClickListener {
                hideKeyboard()
            }
            requestsSearchBarSearchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        setupFilterObserver(newText)
                    }
                    return true
                }
            })
            swipeRefresh.setOnRefreshListener {
                viewModel.onRefresh()
                swipeRefresh.hideRefresh()
            }
        }
    }

    private fun setupFilterObserver(newString: String) {
        with(viewModel) {
            onFilteringCards(newString)
            cards.observe(viewLifecycleOwner) { cards ->
                cards?.let {
                    adapter.verifyCards()
                }
            }
        }
    }

    private fun isDonePressed(id: Int) = id == EditorInfo.IME_ACTION_DONE
}