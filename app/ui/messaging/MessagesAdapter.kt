package com.ibm.internship.beabee.ui.messaging

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.databinding.CardMessagesBinding
import com.ibm.internship.beabee.domain.models.Message
import com.ibm.internship.beabee.utils.Constants.Companion.TEL

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {
    private var messages: List<Message> = emptyList()

    fun setMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding =
            CardMessagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.apply {
            setTextViews(message)
            setRandomUserImageColor(userImageView, itemView.context)
            phoneIconImageView.setOnClickListener { launchDialer(message.phoneNumber) }
        }
    }

    class MessageViewHolder(binding: CardMessagesBinding) : RecyclerView.ViewHolder(binding.root) {
        private val senderNameTextView: TextView = binding.senderNameTextView
        private val lastMessageTextView: TextView = binding.lastMessageTextView
        private val initialsTextView: TextView = binding.initialsTextView
        val phoneIconImageView: ImageView = binding.phoneIconImageView
        val userImageView: ImageView = binding.userImage

        fun setTextViews(message: Message) {
            this.apply {
                message.apply {
                    senderNameTextView.text = senderName
                    lastMessageTextView.text = lastMessage
                    initialsTextView.text = initials
                }
            }
        }

        fun launchDialer(phoneNumber: String) {
            val dial = Uri.parse("$TEL$phoneNumber")
            val dialIntent = Intent(Intent.ACTION_DIAL, dial)
            this.itemView.context.startActivity(dialIntent)
        }
    }

    private fun setRandomUserImageColor(imageView: ImageView, context: Context) {
        val randomColor = listOf(
            R.color.teal_100,
            R.color.teal_200,
            R.color.teal_500,
            R.color.teal_600,
            R.color.teal_700,
        ).random()
        val color = ContextCompat.getColor(context, randomColor)
        imageView.setColorFilter(color)
    }
}

