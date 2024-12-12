package com.example.menuadvisor.presentation.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.data.local.dao.FavoriteDao
import com.example.menuadvisor.data.local.entity.FavoriteEntity
import com.example.menuadvisor.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val favoriteDao: FavoriteDao,
    private val placeRepository: PlaceRepository
) : ViewModel() {
    private val _favorites = MutableLiveData<List<PlaceData>?>()
    val favorites: LiveData<List<PlaceData>?> = _favorites

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                if (userId != null) {
                    observeFavorites(userId)
                }
            }
        }
    }

    private fun observeFavorites(userId: String) {
        viewModelScope.launch {
            favoriteDao.getFavoritesByUserId(userId).collect { favoriteEntities ->
                // Favori yerlerin detaylarını API'den al
                val favoritePlaces = mutableListOf<PlaceData>()
                favoriteEntities.forEach { entity ->
                    try {
                        val response = placeRepository.getPlace(entity.placeId)
                        if (response.isSuccessful) {
                            response.body()?.data?.let { place ->
                                favoritePlaces.add(place)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("FavoritesViewModel", "Error fetching place details: ${e.message}")
                    }
                }
                _favorites.value = favoritePlaces
            }
        }
    }

    fun addFavorite(placeId: Int) {
        viewModelScope.launch {
            val userId = userPreferences.userId.first()
            if (userId != null) {
                val favorite = FavoriteEntity(
                    userId = userId,
                    placeId = placeId
                )
                favoriteDao.insertFavorite(favorite)
            }
        }
    }

    fun removeFavorite(placeId: Int) {
        viewModelScope.launch {
            val userId = userPreferences.userId.first()
            if (userId != null) {
                favoriteDao.deleteFavorite(userId, placeId)
            }
        }
    }

    fun isFavorite(placeId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = userPreferences.userId.first()
            if (userId != null) {
                favoriteDao.isFavorite(userId, placeId).collect { isFavorite ->
                    callback(isFavorite)
                }
            }
        }
    }
}