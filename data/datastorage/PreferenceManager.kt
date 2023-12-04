package com.ibm.beabee.data.datastorage

import android.content.SharedPreferences
import com.ibm.beabee.data.utils.Constants.IS_FIRST_LOGIN
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(private val sharedPreferences: SharedPreferences) {
    var isFirstLogin: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_LOGIN, false)
        set(value) = sharedPreferences.edit().putBoolean(IS_FIRST_LOGIN, value).apply()
}