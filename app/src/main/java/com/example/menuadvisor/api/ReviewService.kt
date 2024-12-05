package com.example.menuadvisor.api

import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.ReviewData
import com.example.menuadvisor.model.ReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReviewService {

    @GET("api/v1/Review")
    suspend fun getReviews(
        @Query("PageNumber") pageNumber: Int? = 1,
        @Query("PageSize") pageSize: Int? = 10,
        @Query("ProductId") productId: Int? = null,
        @Query("PlaceId") placeId: Int? = null,
        @Query("RateNumber") rateNumber: Int? = null,
    ): Response<ApiResponse<List<ReviewData>>>


    @POST("api/v1/Review")
    suspend fun postReview(
        @Body requestBody: ReviewRequest
    ): Response<ApiResponse<Int>>
}