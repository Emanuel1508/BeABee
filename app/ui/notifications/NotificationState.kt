package com.ibm.internship.beabee.ui.notifications

import com.ibm.internship.beabee.R

sealed class NotificationsState(val resource: Int) {
    data object NewNotifications: NotificationsState(R.drawable.ic_new_notifications)
    data object OldNotifications: NotificationsState(R.drawable.ic_notifications)
}