package com.ibm.beabee.data.network

import com.ibm.beabee.data.network.models.RequestItem
import com.ibm.beabee.data.network.models.RequestsDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RequestApi {
    @GET("/requests")
    suspend fun getAllPendingRequests(): RequestsDTO

    @GET("/request/details")
    suspend fun getRequestDetails(@Query("requestId") requestId: String): RequestItem

    @POST("/request/activate")
    suspend fun activateRequest(
        @Query("requestId") requestId: String,
        @Query("userId") userId: String
    ): Result<Unit>

    @GET("/my_requests")
    suspend fun getMyRequests(@Query(value = "requesterId") requesterId: String): RequestsDTO

    @POST("request/feedback")
    suspend fun submitFeedback(
        @Query("requestId") requestId: String,
        @Query("userId") userId: String,
        @Query("rating") rating: Float
    )

    @POST("request/finish")
    suspend fun finishRequest(
        @Query("requestId") requestId: String,
        @Query("userId") userId: String
    )
}