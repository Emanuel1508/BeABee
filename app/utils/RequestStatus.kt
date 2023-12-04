package com.ibm.internship.beabee.utils

import com.ibm.internship.beabee.R

enum class RequestStatus(val stringResource: Int) {
    PENDING(R.string.pending_status),
    FINISHED(R.string.finished_status),
    APPROVED(R.string.in_progress_status)
}