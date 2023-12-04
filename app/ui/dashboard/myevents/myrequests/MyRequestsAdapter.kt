package com.ibm.internship.beabee.ui.dashboard.myevents.myrequests

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.databinding.ChipTagRequestBinding
import com.ibm.internship.beabee.databinding.ItemRequestBinding
import com.ibm.internship.beabee.domain.models.GetMyRequestResponse
import com.ibm.internship.beabee.utils.Constants
import com.ibm.internship.beabee.utils.Constants.Companion.DEFAULT_RATING
import com.ibm.internship.beabee.utils.Constants.Companion.VISIBLE_CODE
import com.ibm.internship.beabee.utils.RequestStatus
import com.ibm.internship.beabee.utils.gone
import com.ibm.internship.beabee.utils.setIcon
import com.ibm.internship.beabee.utils.setVisibility

class MyRequestsAdapter(
    private val onMyRequestEvent: (MyRequestEvent) -> Unit
) : RecyclerView.Adapter<MyRequestsAdapter.RequestsViewHolder>() {

    private val listRequests = ArrayList<GetMyRequestResponse>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestsViewHolder {
        val binding = ItemRequestBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        val request = listRequests[position]
        holder.apply {
            setupViews(request)
            setupChips(request)
            setupStatus()
            setupListeners(request)
        }
    }

    override fun getItemCount() = listRequests.size

    fun setRequests(listRequests: List<GetMyRequestResponse>) {
        this.listRequests.clear()
        this.listRequests.addAll(listRequests)
        notifyDataChanged()
    }

    private fun notifyDataChanged() = notifyItemRangeChanged(
        Constants.START_POSITION,
        listRequests.size
    )

    inner class RequestsViewHolder(private val binding: ItemRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setupViews(request: GetMyRequestResponse) {
            with(binding) {
                request.apply {
                    val context = titleTextView.context
                    titleTextView.text = title
                    locationTextView.text = location
                    statusTextView.text = status
                    timeTextView.text = context.getDateOrDefault(date)
                    hero?.apply {
                        userAvatar.initialsTextView.text = nameInitials
                        userNameTextView.text = name
                        numberRatingTextView.text =
                            ratingNumber?.toString() ?: DEFAULT_RATING.toString()
                        userRatingBar.rating = ratingNumber ?: DEFAULT_RATING
                    }
                }
            }
        }

        fun setupListeners(request: GetMyRequestResponse) {
            with(binding) {
                deleteRequestButton.setOnClickListener {
                    onMyRequestEvent(MyRequestEvent.OnDelete)
                }
                doneRequestButton.setOnClickListener {
                    onDoneRequestClick(request)
                }
                contactButton.setOnClickListener {
                    onContactHeroClick(request)
                }
                expandHelperButton.setOnClickListener {
                    expandHelper(isExpanded = helperLayout.visibility != VISIBLE_CODE)
                }
            }
        }

        fun setupChips(request: GetMyRequestResponse) {
            with(binding) {
                chipsContainer.removeAllViews()
                request.chips.forEach { tag ->
                    val chip = ChipTagRequestBinding.inflate(
                        LayoutInflater.from(chipsContainer.context),
                        chipsContainer,
                        true
                    )
                    chip.root.text = tag
                }
            }
        }

        fun setupStatus() {
            with(binding) {
                val currentStatus = statusTextView.text.toString()
                val isRequestStatusApproved = currentStatus.isStatusApproved(statusTextView.context)
                expandHelper(isRequestStatusApproved)
                expandHelperButton.setVisibility(isRequestStatusApproved)
                if (!isRequestStatusApproved) {
                    doneRequestButton.gone()
                }
            }
        }

        private fun expandHelper(isExpanded: Boolean) {
            with(binding) {
                helperLayout.setVisibility(isExpanded)
                expandHelperButton.setIcon(
                    isExpanded,
                    R.drawable.ic_expand_less_24px,
                    R.drawable.ic_expand_more_24px
                )
            }
        }

        private fun onDoneRequestClick(request: GetMyRequestResponse) {
            request.hero?.apply {
                onMyRequestEvent(
                    MyRequestEvent.OnSetIsDone(
                        RatingData(
                            request.id,
                            id,
                            name,
                            nameInitials,
                        )
                    )
                )
            } ?: onMyRequestEvent(MyRequestEvent.OnFailure)
        }
    }

    private fun onContactHeroClick(request: GetMyRequestResponse) {
        request.hero?.phone?.let {
            onMyRequestEvent(MyRequestEvent.OnContact(it))
        } ?: onMyRequestEvent(MyRequestEvent.OnFailure)
    }

    private fun String.isStatusApproved(context: Context) =
        this.uppercase() == context.getString(RequestStatus.APPROVED.stringResource).uppercase()

    private fun Context.getDateOrDefault(date: String) = getString(
        R.string.my_request_date,
        date.ifEmpty { getString(R.string.default_text) }
    )
}