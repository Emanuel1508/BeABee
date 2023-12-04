package com.ibm.internship.beabee.domain.models

data class GetRequestResponse (
    val id: String,
    val title: String,
    val location: String,
    val chips: List<String>,
    val description: String
)
fun GetRequestResponse.containsFilteringString(inputString: String): Boolean {
    return (this.title.lowercase().contains(inputString)
            || this.description.lowercase().contains(inputString)
            || this.location.lowercase().contains(inputString)
            || this.chips.any { it.contains(inputString) })
}