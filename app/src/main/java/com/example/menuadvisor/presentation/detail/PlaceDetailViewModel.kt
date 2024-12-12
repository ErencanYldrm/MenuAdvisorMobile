package com.example.menuadvisor.presentation.detail_screens.place

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.repository.PlaceRepository
import com.example.menuadvisor.repository.ProductRepository
import com.example.menuadvisor.repository.ReviewRepository
import com.example.menuadvisor.utils.LocationUtils
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// PlaceDetailViewModel.kt
@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    val placeId = MutableLiveData<Int>()
    val place = MutableLiveData<PlaceData?>()
    val productList = MutableLiveData<List<ProductData>>()
    val reviewCount = MutableLiveData<Int>()
    
    private val _distance = MutableStateFlow<Float?>(null)
    val distance: StateFlow<Float?> = _distance

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    fun updateUserLocation(context: Context) {
        viewModelScope.launch {
            val location = LocationUtils.getCurrentLocation(context)
            location?.let {
                _userLocation.value = LatLng(it.latitude, it.longitude)
                calculateDistance()
            }
        }
    }

    private fun calculateDistance() {
        viewModelScope.launch {
            try {
                val currentLocation = _userLocation.value ?: return@launch
                val placeData = place.value ?: return@launch
                
                val latitude = placeData.lat?.replace(",", ".")?.toDoubleOrNull()
                val longitude = placeData.lon?.replace(",", ".")?.toDoubleOrNull()
                
                if (latitude != null && longitude != null) {
                    val distance = LocationUtils.calculateDistance(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        latitude,
                        longitude
                    )
                    _distance.value = distance
                }
            } catch (e: Exception) {
                Log.e("PlaceDetail", "Error calculating distance: ${e.message}")
            }
        }
    }

    fun getPlace() {
        placeId.value?.let {
            viewModelScope.launch {
                val response = placeRepository.getPlace(it)
                if (response.isSuccessful) {
                    place.postValue(response.body()?.data as PlaceData?)
                    getReviewCount(it)
                    calculateDistance() // Mekan bilgisi geldiğinde mesafeyi hesapla
                }
            }
        }
    }

    private fun getReviewCount(placeId: Int) {
        viewModelScope.launch {
            try {
                val response = reviewRepository.getReviewsByPlaceId(placeId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { reviews ->
                        reviewCount.postValue((reviews as List<*>).size)
                    }
                }
            } catch (e: Exception) {
                Log.e("PlaceDetail", "Error getting review count: ${e.message}")
            }
        }
    }

    fun getProductsByPlaceId() {
        placeId.value?.let {
            viewModelScope.launch {
                try {
                    val response = productRepository.getProductsByPlaceId(it)
                    if (response.isSuccessful) {
                        response.body()?.data?.let { data ->
                            productList.postValue(data as List<ProductData>)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PlaceDetail", "Error getting products: ${e.message}")
                }
            }
        }
    }
}


