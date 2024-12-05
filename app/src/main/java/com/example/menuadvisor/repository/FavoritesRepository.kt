package com.example.menuadvisor.repository

import com.example.menuadvisor.api.FavoritesService
import com.example.menuadvisor.model.FavoriteRequest
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val favoritesService: FavoritesService
) {
    suspend fun addFavorite(userId: String, placeId: Int) =
        favoritesService.addFavorite(
            FavoriteRequest(
                userId = userId,
                placeId = placeId
            )
        )

    suspend fun removeFavorite(id: Int) =
        favoritesService.removeFavorite(id)

    suspend fun getFavorites(userId: String) =
        favoritesService.getFavorites(userId)
}