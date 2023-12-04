package com.ibm.internship.beabee.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.databinding.ItemNotificationTypeOneBinding
import com.ibm.internship.beabee.databinding.ItemNotificationTypeThreeBinding
import com.ibm.internship.beabee.databinding.ItemNotificationTypeTwoBinding
import com.ibm.internship.beabee.domain.models.GetNotificationResponse
import com.ibm.internship.beabee.utils.Constants
import com.ibm.internship.beabee.domain.models.NotificationType.SOMEONE_WANTS_TO_HELP
import com.ibm.internship.beabee.domain.models.NotificationType.BADGE
import com.ibm.internship.beabee.domain.models.NotificationType.SOMEONE_NEEDS_HELP
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val notifications = ArrayList<GetNotificationResponse>()

    override fun getItemViewType(position: Int) = notifications[position].type.index

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        SOMEONE_NEEDS_HELP.index -> {
            val binding = ItemNotificationTypeOneBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            NotificationTypeOneViewHolder(binding)
        }

        BADGE.index -> {
            val binding = ItemNotificationTypeTwoBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            NotificationTypeTwoViewHolder(binding)
        }

        SOMEONE_WANTS_TO_HELP.index -> {
            val binding = ItemNotificationTypeThreeBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            NotificationTypeThreeViewHolder(binding)
        }

        else -> throw IllegalArgumentException("Invalid type: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            SOMEONE_NEEDS_HELP.index -> holder as? NotificationTypeOneViewHolder
            BADGE.index -> holder as? NotificationTypeTwoViewHolder
            SOMEONE_WANTS_TO_HELP.index -> holder as? NotificationTypeThreeViewHolder
            else -> null
        }?.apply {
            setupViews(notifications[position])
        }
    }

    override fun getItemCount() = notifications.size

    fun setNotifications(notifications: List<GetNotificationResponse>) {
        this.notifications.clear()
        this.notifications.addAll(notifications)
        notifyDataChanged()
    }

    private fun notifyDataChanged() = notifyItemRangeChanged(
        Constants.START_POSITION,
        notifications.size
    )

    abstract class NotificationType(open val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun setupViews(notification: GetNotificationResponse)
    }

    inner class NotificationTypeOneViewHolder(override val binding: ItemNotificationTypeOneBinding) :
        NotificationType(binding) {

        override fun setupViews(notification: GetNotificationResponse) {
            with(binding) {
                val context = titleNotificationTextView.context
                notificationTimeTextView.text = notification.time.formatNotificationTime(context)
                titleNotificationTextView.text = context.getString(
                    R.string.title_notification_type_one,
                    notification.numberPeopleInNeedAround.toString()
                )
            }
        }
    }

    inner class NotificationTypeTwoViewHolder(override val binding: ItemNotificationTypeTwoBinding) :
        NotificationType(binding) {

        override fun setupViews(notification: GetNotificationResponse) {
            with(binding) {
                val context = notificationTimeTextView.context
                notificationTimeTextView.text = notification.time.formatNotificationTime(context)
                badgeNameTextView.text = notification.badgeName
            }
        }
    }

    inner class NotificationTypeThreeViewHolder(override val binding: ItemNotificationTypeThreeBinding) :
        NotificationType(binding) {

        override fun setupViews(notification: GetNotificationResponse) {
            with(binding) {
                val context = notificationTimeTextView.context
                notificationTimeTextView.text = notification.time.formatNotificationTime(context)
                descriptionNotificationTextView.text = notification.description
                heroNameTextView.text = notification.hero?.name
                heroAvatar.initialsTextView.text = notification.hero?.nameInitials
            }
        }
    }

    private companion object {
        const val MINUTE_1 = 1
        const val SECONDS_PER_MINUTE = 60
        const val MILLISECONDS_PER_SECOND = 1000
        const val MINUTES_PER_HOUR = 60
        const val HOURS_PER_DAY = 24
        const val DAYS_PER_WEEK = 7
    }

    private fun String.formatNotificationTime(context: Context): String {
        try {
            val currentTimeMillis = System.currentTimeMillis()
            val providedTimeMillis = this.toLong()
            val diffMillis = currentTimeMillis - providedTimeMillis

            val minutes = (diffMillis / (MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE)).toInt()
            val hours = (minutes / MINUTES_PER_HOUR)
            val days = (hours / HOURS_PER_DAY)

            return when {
                minutes <= MINUTE_1 -> context.getString(R.string.just_now_time)
                minutes < MINUTES_PER_HOUR -> context.getString(
                    R.string.minutes_ago_time,
                    minutes.toString()
                )

                hours < HOURS_PER_DAY -> context.getString(
                    R.string.hours_ago_time,
                    hours.toString()
                )

                days < DAYS_PER_WEEK -> context.getString(R.string.days_ago_time, days.toString())
                else -> {
                    val sdf = SimpleDateFormat(
                        com.ibm.internship.beabee.domain.utils.Constants.DATE_FORMAT,
                        Locale.getDefault()
                    )
                    sdf.format(Date(this.toLong()))
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            return context.getString(R.string.default_text)
        }
    }
}