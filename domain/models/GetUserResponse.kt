package com.ibm.internship.beabee.domain.models

data class GetUserResponse(
    val type: String,
    val userId: String,
    val badge: Int,
    val ratingAvg: Float,
    val nrVotes: Int,
    val email: String,
    val name: String,
    val phone: String,
    val tags: List<String>
)