package com.ibm.internship.beabee.domain.models

data class GetMyRequestResponse(
    val id: String,
    val title: String,
    val location: String,
    val status: String,
    val date: String,
    val chips: List<String>,
    val hero: Hero?
)

data class Hero(
    val id: String?,
    val name: String?,
    val nameInitials: String?,
    val phone: String?,
    val ratingNumber: Float?
)