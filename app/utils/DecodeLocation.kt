package com.ibm.internship.beabee.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.utils.Constants.Companion.GEOCODER_ERROR
import com.ibm.internship.beabee.utils.Constants.Companion.INVALID_ADDRESS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class DecodeLocation @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(): UseCaseResponse<String> {
        return try {
            val location = withContext(Dispatchers.IO) {
                fusedLocationClient.lastLocation.await()
            }
            location?.let { UseCaseResponse.Success(updateLocationDetails(it)) }
                ?: UseCaseResponse.Failure(ErrorMessage.INVALID_ADDRESS)
        } catch (exception: Exception) {
            UseCaseResponse.Failure(ErrorMessage.GENERAL)
        }
    }

    private fun updateLocationDetails(location: Location) = try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]?.getAddressLine(0)
            val province = addresses[0]?.adminArea
            "$address, $province"
        } else {
            INVALID_ADDRESS
        }
    } catch (exception: IOException) {
        exception.printStackTrace()
        GEOCODER_ERROR
    }
}