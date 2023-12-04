package com.ibm.internship.beabee.ui.dashboard.myevents.myrequests

sealed class MyRequestEvent {
    data object OnDelete : MyRequestEvent()
    data object OnFailure : MyRequestEvent()
    data class OnContact(val phoneNumber: String) : MyRequestEvent()
    data class OnSetIsDone(val ratingData: RatingData) : MyRequestEvent()
    data class OnAllDone(val requestId: String?, val userId: String?, val rating: Float) :
        MyRequestEvent()
}
