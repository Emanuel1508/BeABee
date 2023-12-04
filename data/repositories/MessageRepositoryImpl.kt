package com.ibm.beabee.data.repositories

import com.ibm.internship.beabee.domain.models.Message
import com.ibm.internship.beabee.domain.repositories.MessageRepository
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(

) : MessageRepository {
    override fun getMessages(): List<Message> =
        listOf(
            Message(
                "John Smith",
                "So can you help me build a house?",
                "JS",
                "+0786564346"
            ),
            Message(
                "Tony Stark",
                "You did a great job with that task, thank you! I will write more to see that the text stays on two lines and ends with ellipsis",
                "TS",
                "+07443644543"
            ),
            Message(
                "Saul Goodman",
                "If you are even in need of a great lawyer you know who to call.",
                "SG",
                "+07488844543"
            ),
        )
}