package com.ibm.internship.beabee.utils

class Constants {
    companion object {
        const val NAME_REGEX = "^[a-zA-Z]{1,30}$"
        const val PHONE_REGEX = "^[+][0-9]{11,}$"
        const val PASSWORD_REGEX = "^(?=.*[a-zA-Z0-9])(?=.*[@#$%^&+=!])(?=.*[0-9]).{8,}$"
        const val EMAIL_REGEX = "[A-Za-z0-9._]+@[A-Za-z0-9._]+\\.[A-Za-z]{2,}"
        const val GOOGLE_MAPS_LINK = "http://maps.google.com/maps"
        const val MARKET_LINK = "market://details"
        const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
        const val EMPTY_STRING = ""
        const val TEL = "tel:"
        const val VISIBLE_CODE = 0
        const val START_POSITION = 0
        const val DEFAULT_PEOPLE_HELPED = 0
        const val NUMBER_INITIAL_TAGS = 4
        const val MAX_RESULTS = 5
        const val REGISTER_FIELDS_MAX_LENGTH = 30
        const val TIME_DELAY: Long = 3000
        const val MAP_ZOOM = 15f
        const val DEFAULT_RATING = 0f
        const val INVALID_ADDRESS = "Invalid address"
        const val GEOCODER_ERROR = "Geocoder error"
    }
}