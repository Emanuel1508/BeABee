package com.ibm.beabee.data.network

import com.ibm.internship.beabee.domain.models.GetUserResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @POST("user/update")
    suspend fun updateUser(
        @Query(value = "userId") userId: String,
        @Query(value = "email") email: String?,
        @Query(value = "name") name: String?,
        @Query(value = "phone") phone: String?,
        @Query(value = "tags") tags: ArrayList<String>?,
    )

    @GET("/user")
    suspend fun getUser(@Query("userId") userId: String): GetUserResponse
}
