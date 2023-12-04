package com.ibm.internship.beabee.ui.messaging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.models.Message
import com.ibm.internship.beabee.domain.usecases.GetMessagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val messagesUseCase: GetMessagesUseCase,
) : BaseViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    init {
        setupMessages()
    }

    private fun setupMessages() {
        val allPossibleMessages = messagesUseCase()
        val randomSize = Random.nextInt(0, allPossibleMessages.size + 1)
        _messages.value = allPossibleMessages.shuffled().take(randomSize)
    }
}