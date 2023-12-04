package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.MessageRepository

class GetMessagesUseCase(private val messageRepository: MessageRepository) {
    operator fun invoke() = messageRepository.getMessages()
}