package com.kad.power.data.remote

import com.kad.power.data.local.entities.InquirySyncEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface KADApiService {
    @GET("products")
    suspend fun fetchLatestProducts(): Response<List<ProductNetworkModel>>

    @POST("inquiries")
    suspend fun submitInquiry(@Body inquiry: InquirySyncEntity): Response<Unit>
}
