package com.example.menuadvisor.presentation.find

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.data.local.dao.FavoriteDao
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.repository.PlaceRepository
import com.example.menuadvisor.utils.LocationUtils
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: UserPreferences,
    private val favoriteDao: FavoriteDao
) : ViewModel() {

    private val _places = MutableStateFlow<List<PlaceData>>(emptyList())
    val places: StateFlow<List<PlaceData>> = _places

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    private val _favoritePlaceIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoritePlaceIds: StateFlow<Set<Int>> = _favoritePlaceIds

    init {
        getAllPlaces()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                if (userId != null) {
                    favoriteDao.getFavoritesByUserId(userId)
                        .map { favorites -> favorites.map { it.placeId }.toSet() }
                        .collect { favoriteIds ->
                            _favoritePlaceIds.value = favoriteIds
                        }
                }
            }
        }
    }

    fun updateUserLocation(context: Context) {
        viewModelScope.launch {
            val location = LocationUtils.getCurrentLocation(context)
            location?.let {
                _userLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    private fun getAllPlaces() {
        viewModelScope.launch {
            try {
                val response = placeRepository.getPlaces()
                if (response.isSuccessful) {
                    response.body()?.data?.let { places ->
                        _places.value = places
                    }
                }
            } catch (e: Exception) {
                // Hata durumunu handle et
            }
        }
    }
}