package com.example.menuadvisor.api

import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.PlaceData
import retrofit2.Response
import retrofit2.http.*

interface PlaceService {

    @GET("api/v1/Place")
    suspend fun getPlaces(
        @Query("PageNumber") pageNumber: Int? = 1,
        @Query("PageSize") pageSize: Int? = 10,
        @Query("Name") name: String? = null,
        @Query("District") district: String? = null,
        @Query("RateSort") rateSort: Boolean? = null,
    ): Response<ApiResponse<List<PlaceData>>>

    @GET("api/v1/Place/{id}")
    suspend fun getPlace(
        @Path("id") id: Int
    ): Response<ApiResponse<PlaceData>>

    @PUT("api/v1/Place/{id}")
    suspend fun updatePlace(
        @Path("id") id: Int,
        @Body place: PlaceData
    ): Response<ApiResponse<PlaceData>>
}