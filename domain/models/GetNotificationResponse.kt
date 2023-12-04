package com.ibm.internship.beabee.domain.models

data class GetNotificationResponse(
    val notificationId: String,
    val userId: String,
    val type: NotificationType,
    val time: String,
    val isRead: Boolean,
    val numberPeopleInNeedAround: Int? = null,
    val badgeName: String? = null,
    val description: String? = null,
    val hero: Hero? = null
)

enum class NotificationType(val index: Int) {
    SOMEONE_NEEDS_HELP(0),
    BADGE(1),
    SOMEONE_WANTS_TO_HELP(2)
}