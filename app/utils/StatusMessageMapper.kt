package com.ibm.internship.beabee.utils

import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.domain.utils.StatusMessage


fun StatusMessage.mapToPresentation(): Pair<Int, Int> {
    return when (this) {
        StatusMessage.PENDING -> Pair(
            R.string.pending_text,
            R.drawable.text_view_orange_rounded_corners
        )
        StatusMessage.PROGRESS -> Pair(
            R.string.in_progress_text,
            R.drawable.text_view_green_rounded_corners
        )
    }
}