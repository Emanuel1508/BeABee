package com.ibm.internship.beabee.domain.models

data class RequestDetailsResponse(
    val id: String,
    val date: String,
    val description: String,
    val location: String,
    val lat:String,
    val long:String,
    val notes: String,
    val requesterName: String,
    val requesterPhone: String,
    val requesterId: String,
    val chips: List<String>,
    val status: String
)