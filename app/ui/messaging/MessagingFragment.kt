package com.ibm.internship.beabee.ui.messaging

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentMessagingBinding
import com.ibm.internship.beabee.utils.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagingFragment :
    BaseFragment<FragmentMessagingBinding>(FragmentMessagingBinding::inflate) {
    private val viewModel: MessagingViewModel by viewModels()
    private lateinit var adapter: MessagesAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMessagesAdapter()
    }

    private fun setupMessagesAdapter() {
        val recyclerView: RecyclerView = binding.recyclerView
        adapter = MessagesAdapter()
        recyclerView.adapter = adapter
        viewModel.messages.observe(viewLifecycleOwner) { messageList ->
            adapter.setMessages(messageList)
            toggleNoMessagesTextVisibility(messageList.isEmpty())
        }
    }

    private fun toggleNoMessagesTextVisibility(isZero: Boolean) {
        with(binding) {
            noMessagesTitleTextView.setVisibility(isZero)
            noMessagesDescriptionTextView.setVisibility(isZero)
        }
    }
}