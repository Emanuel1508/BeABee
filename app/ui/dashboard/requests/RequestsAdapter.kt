package com.ibm.internship.beabee.ui.dashboard.requests

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.ibm.internship.beabee.databinding.CardRequestsBinding
import com.ibm.internship.beabee.databinding.ChipTagRequestBinding
import com.ibm.internship.beabee.domain.models.GetRequestResponse
import com.ibm.internship.beabee.utils.Constants.Companion.START_POSITION

class RequestsAdapter(private var onRequestItemClicked: ((String) -> Unit)) :
    RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {
    private val cards = mutableListOf<GetRequestResponse>()

    fun setCards(cards: Collection<GetRequestResponse>) {
        this.cards.clear()
        this.cards.addAll(cards)
        notifyItemRangeChanged(START_POSITION, cards.size)
    }

    fun verifyCards() = notifyDataSetChanged()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardRequestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        setTextViews(holder, card)
        setChips(holder, card)
        setListener(holder, card.id)
    }

    override fun getItemCount() = cards.size

    private fun setChips(holder: ViewHolder, card: GetRequestResponse) {
        holder.chipsContainer.removeAllViews()
        card.chips.forEach { text ->
            val chip = ChipTagRequestBinding.inflate(
                LayoutInflater.from(holder.chipsContainer.context),
                holder.chipsContainer, true
            )
            chip.root.text = text
        }
    }

    private fun setTextViews(holder: ViewHolder, card: GetRequestResponse) {
        holder.apply {
            card.apply {
                titleTextView.text = title
                locationTextView.text = location.uppercase()
                descriptionTextView.text = description
            }
        }
    }

    class ViewHolder(binding: CardRequestsBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleTextView: TextView = binding.title
        val locationTextView: TextView = binding.location
        val descriptionTextView: TextView = binding.description
        val chipsContainer: ChipGroup = binding.chipsContainer
        val helpButton: Button = binding.helpButton
    }

    private fun setListener(holder: ViewHolder, requestId: String) {
        holder.helpButton.setOnClickListener {
            onRequestItemClicked.invoke(requestId)
        }
    }
}