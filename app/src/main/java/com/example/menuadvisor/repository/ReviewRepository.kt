package com.example.menuadvisor.repository

import android.util.Log
import com.example.menuadvisor.api.ReviewService
import com.example.menuadvisor.model.ReviewRequest
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewService: ReviewService
) {
    suspend fun getAllReviews(
        pageNumber: Int? = 1,
        pageSize: Int? = 10,
        productId : Int? = null
    ) = reviewService.getReviews(
        pageNumber = pageNumber,
        pageSize = pageSize,
        productId = productId
    )

    suspend fun getReviewsByProductId(
        productId: Int
    ) = reviewService.getReviews(
        productId = productId
    )

    suspend fun getReviewsByPlaceId(
        placeId: Int
    ) = reviewService.getReviews(
        placeId = placeId
    )

    suspend fun getReviewsByRateNumber(
        rateNumber: Int,
        productId: Int
    ) = reviewService.getReviews(
        rateNumber = rateNumber,
        productId = productId
    )

    suspend fun postReview(
        reviewRequest: ReviewRequest
    ) = reviewService.postReview(requestBody = reviewRequest)

}