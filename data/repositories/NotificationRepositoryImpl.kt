package com.ibm.beabee.data.repositories

import com.ibm.internship.beabee.domain.models.GetNotificationResponse
import com.ibm.internship.beabee.domain.models.Hero
import com.ibm.internship.beabee.domain.models.NotificationType
import com.ibm.internship.beabee.domain.repositories.NotificationRepository
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    override suspend fun getNotifications(userId: String): List<GetNotificationResponse> {
        delay(1000)
        val currentDate = System.currentTimeMillis()
        val fourMinutesAgo = getCurrentDateMinusMinutes()
        val sixDaysAgo = getCurrentDateMinusDays()

        return listOf(
            GetNotificationResponse(
                notificationId = "notificationIdTypeBadge",
                userId = "connectedUserId",
                type = NotificationType.BADGE,
                time = currentDate.toString(),
                isRead = true,
                badgeName = "Super hero"
            ),
            GetNotificationResponse(
                notificationId = "asd",
                userId = "connectedUserId",
                type = NotificationType.SOMEONE_NEEDS_HELP,
                time = fourMinutesAgo.toString(),
                isRead = true,
                numberPeopleInNeedAround = 22223
            ),
            GetNotificationResponse(
                notificationId = "asd",
                userId = "connectedUserId",
                type = NotificationType.SOMEONE_WANTS_TO_HELP,
                time = "1692201796347",
                description = "I need a coffee",
                isRead = true,
                hero = Hero(
                    id = "justAHero",
                    name = "Tony Stark",
                    nameInitials = "JS",
                    phone = "+203902390293",
                    ratingNumber = 2.3f
                )
            ),
            GetNotificationResponse(
                notificationId = "asd",
                userId = "connectedUserId",
                type = NotificationType.SOMEONE_WANTS_TO_HELP,
                time = sixDaysAgo.toString(),
                description = "I need a new shield",
                isRead = false,
                hero = Hero(
                    id = "justAHero",
                    name = "Captain America",
                    nameInitials = "JS",
                    phone = "+203902390293",
                    ratingNumber = 2.3f
                )
            )
        )
    }

    private fun getCurrentDateMinusMinutes(): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.MINUTE, -4)
        return calendar.timeInMillis
    }

    private fun getCurrentDateMinusDays(): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -6)
        return calendar.timeInMillis
    }
}