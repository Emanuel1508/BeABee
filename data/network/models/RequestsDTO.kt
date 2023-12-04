package com.ibm.beabee.data.network.models

import com.ibm.internship.beabee.domain.models.GetMyRequestResponse
import com.ibm.internship.beabee.domain.models.GetRequestResponse
import com.ibm.internship.beabee.domain.models.Hero
import com.ibm.internship.beabee.domain.models.RequestDetailsResponse
import com.ibm.internship.beabee.domain.utils.formatDate
import com.ibm.internship.beabee.domain.utils.getInitials

data class RequestsDTO(
    val requests: List<RequestItem>
)

data class RequestItem(
    val _id: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val date: String,
    val description: String,
    val location: Location,
    val notes: String,
    val requesterId: String,
    val type: String,
    val status: String,
    val heroId: String?,
    val requester: UserDetails,
    val hero: UserDetails?,
    val tags: List<String>
)

data class Location(
    val address: String,
    val lat: String,
    val long: String
)

data class UserDetails(
    val name: String,
    val phone: String,
    val receivedRating: String?,
    val receivedComment: String
)

fun RequestItem.toGetRequestResponse(): RequestDetailsResponse =
    RequestDetailsResponse(
        id = this._id,
        date = this.date,
        description = this.description,
        location = this.location.address,
        lat = this.location.lat,
        long = this.location.long,
        notes = this.notes,
        requesterName = this.requester.name,
        requesterPhone = this.requester.phone,
        requesterId = this.requesterId,
        chips = this.tags,
        status = this.status
    )

fun RequestsDTO.toMyRequestResponseDomain(): List<GetMyRequestResponse> =
    requests.map { requestItem ->
        GetMyRequestResponse(
            id = requestItem._id,
            title = requestItem.description,
            location = requestItem.location.address,
            chips = requestItem.tags,
            status = requestItem.status,
            date = requestItem.date.formatDate(),
            hero = requestItem.hero?.let { hero ->
                Hero(
                    id = requestItem.heroId,
                    phone = hero.phone,
                    nameInitials = hero.name.getInitials(),
                    name = hero.name,
                    ratingNumber = hero.receivedRating?.toFloat()
                )
            },
        )
    }

fun RequestsDTO.toRequestResponseDomain(): List<GetRequestResponse> =
    requests.map { requestItem ->
        GetRequestResponse(
            id = requestItem._id,
            title = requestItem.description,
            location = requestItem.location.address,
            chips = requestItem.tags,
            description = requestItem.description
        )
    }
