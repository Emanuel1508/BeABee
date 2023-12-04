package com.ibm.internship.beabee.domain.repositories

import com.ibm.internship.beabee.domain.models.Message

interface MessageRepository {
    fun getMessages(): List<Message>
}