package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.NotificationRepository

class GetNotificationsUseCase(private val notificationRepository: NotificationRepository) {
    suspend operator fun invoke(userId: String) = notificationRepository.getNotifications(userId)
}