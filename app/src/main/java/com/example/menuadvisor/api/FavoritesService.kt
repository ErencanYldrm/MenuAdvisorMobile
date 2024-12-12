package com.example.menuadvisor.api

import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.FavoriteRequest
import com.example.menuadvisor.model.FavoriteResponse
import com.example.menuadvisor.model.FavoriteRelation
import retrofit2.Response
import retrofit2.http.*

interface FavoritesService {
    @GET("api/v1/Favorite/{userId}")
    suspend fun getFavorites(@Path("userId") userId: String): Response<FavoriteResponse>

    @GET("api/v1/Favorite/user/{userId}")
    suspend fun getFavoriteRelations(@Path("userId") userId: String): Response<ApiResponse<List<FavoriteRelation>>>

    @POST("api/v1/Favorite")
    suspend fun addFavorite(@Body favoriteRequest: FavoriteRequest): Response<FavoriteResponse>

    @DELETE("api/v1/Favorite/{id}")
    suspend fun removeFavorite(@Path("id") id: Int): Response<FavoriteResponse>
}