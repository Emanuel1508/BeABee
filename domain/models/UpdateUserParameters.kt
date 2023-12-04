package com.ibm.internship.beabee.domain.models

data class UpdateUserParameters(
    var userId: String,
    var name: String?,
    var email: String?,
    val phone: String?,
    var tags: ArrayList<String>?
)
