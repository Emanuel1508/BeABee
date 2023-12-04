package com.ibm.internship.beabee.domain.utils

import com.ibm.internship.beabee.domain.utils.Constants.DATE_FORMAT
import com.ibm.internship.beabee.domain.utils.Constants.EMPTY_STRING
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.getInitials() = this
    .split(" ")
    .joinToString("") { it.first().uppercase() }

fun String.formatDate(): String = try {
    val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    sdf.format(Date(this.toLong()))
} catch (e: Exception) {
    e.printStackTrace()
    EMPTY_STRING
}

fun String.splitFirstLastName(): Pair<String, String>{
    val names = this.split(" ")
    return if (names.size >= 2)
        Pair(names.dropLast(1).joinToString(" "), names.last())
    else Pair("", "")
}