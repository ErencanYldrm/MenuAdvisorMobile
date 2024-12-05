package com.example.menuadvisor.api

import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.FavoriteRequest
import retrofit2.Response
import retrofit2.http.*

interface FavoritesService {
    @GET("api/v1/Favorite/{userId}")
    suspend fun getFavorites(@Path("userId") userId: String): Response<ApiResponse<List<com.example.menuadvisor.model.PlaceData>>>

    @POST("api/v1/Favorite")
    suspend fun addFavorite(@Body favoriteRequest: FavoriteRequest): Response<ApiResponse<Unit>>

    @DELETE("api/v1/Favorite/{id}")
    suspend fun removeFavorite(@Path("id") id: Int): Response<ApiResponse<Unit>>
}