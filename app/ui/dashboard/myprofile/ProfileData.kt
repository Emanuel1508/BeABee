package com.ibm.internship.beabee.ui.dashboard.myprofile

import com.ibm.internship.beabee.utils.Constants.Companion.DEFAULT_PEOPLE_HELPED
import com.ibm.internship.beabee.utils.Constants.Companion.DEFAULT_RATING
import com.ibm.internship.beabee.utils.Constants.Companion.EMPTY_STRING

data class ProfileData(
    val fullName: String = EMPTY_STRING,
    val nameInitials: String = EMPTY_STRING,
    val rating: Float = DEFAULT_RATING,
    val numberPeopleHelped: Int = DEFAULT_PEOPLE_HELPED,
    val tags: List<String>? = emptyList()
)