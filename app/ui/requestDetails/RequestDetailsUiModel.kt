package com.ibm.internship.beabee.ui.requestDetails

import com.google.android.gms.maps.model.LatLng

data class RequestDetailsUiModel(
    val id: String,
    val date: String,
    val description: String,
    val location: String,
    val notes: String,
    val requesterName: String,
    val requesterPhone: String,
    val chips: List<String>,
    val status: String,
    val coordinates: LatLng
)