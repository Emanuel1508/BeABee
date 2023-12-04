package com.ibm.internship.beabee.domain.repositories

import com.ibm.internship.beabee.domain.models.GetNotificationResponse

interface NotificationRepository {
    suspend fun getNotifications(userId: String): List<GetNotificationResponse>
}