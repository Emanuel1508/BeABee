package com.ibm.internship.beabee.ui.dashboard.myprofile

import com.ibm.internship.beabee.R

sealed class ListTagsState(val stringResource: Int) {
    data object ShowMore : ListTagsState(R.string.see_more_label)
    data object ShowLess : ListTagsState(R.string.see_less_label)
}
