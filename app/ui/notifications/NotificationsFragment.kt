package com.ibm.internship.beabee.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentNotificationsBinding
import com.ibm.internship.beabee.utils.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment :
    BaseFragment<FragmentNotificationsBinding>(FragmentNotificationsBinding::inflate) {

    private val viewModel: NotificationsViewModel by activityViewModels()
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupNotificationsAdapter()
        setupObservers()
        setupListeners()
    }

    private fun setupNotificationsAdapter() {
        notificationsAdapter = NotificationsAdapter()
        val recyclerView: RecyclerView = binding.notificationsRecyclerView
        recyclerView.adapter = notificationsAdapter
    }

    private fun setupObservers() {
        with(viewModel) {
            notifications.observe(viewLifecycleOwner) { list ->
                notificationsAdapter.setNotifications(list)
            }
            loadingLiveData.observe(viewLifecycleOwner) {
                binding.notificationsRefresh.isRefreshing = it.shouldShowLoading
                binding.notificationsRecyclerView.setVisibility(!it.shouldShowLoading)
            }
        }
    }

    private fun setupListeners() {
        binding.notificationsRefresh.setOnRefreshListener {
            viewModel.onGetData()
        }
    }
}