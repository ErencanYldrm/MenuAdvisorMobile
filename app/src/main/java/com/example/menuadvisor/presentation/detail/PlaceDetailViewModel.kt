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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

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
                    calculateDistance()
                    updatePlaceRating()
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
        placeId.value?.let { placeId ->
            viewModelScope.launch {
                try {
                    val response = productRepository.getProductsByPlaceId(placeId)
                    if (response.isSuccessful) {
                        response.body()?.data?.let { products ->
                            productList.postValue(products as List<ProductData>)
                            updatePlaceRating()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PlaceDetail", "Error getting products: ${e.message}")
                }
            }
        }
    }

    private fun updatePlaceRating() {
        viewModelScope.launch {
            try {
                val products = productList.value ?: return@launch
                if (products.isEmpty()) {
                    Log.d("PlaceDetail", "No products found for rating calculation")
                    return@launch
                }

                var totalRating = 0.0
                var totalReviews = 0

                products.forEach { product ->
                    Log.d("PlaceDetail", "Processing product ${product.name} with rate: ${product.rate}")
                    val reviewResponse = reviewRepository.getReviewsByProductId(product.id ?: return@forEach)
                    if (reviewResponse.isSuccessful) {
                        reviewResponse.body()?.data?.let { reviews ->
                            val productReviews = reviews as List<*>
                            Log.d("PlaceDetail", "Product ${product.name} has ${productReviews.size} reviews")
                            if (productReviews.isNotEmpty()) {
                                val productRating = product.rate ?: 0.0
                                val productTotalRating = productRating * productReviews.size
                                Log.d("PlaceDetail", "Product ${product.name}: rating=$productRating, reviews=${productReviews.size}, totalRating=$productTotalRating")
                                totalRating += productTotalRating
                                totalReviews += productReviews.size
                            }
                        }
                    } else {
                        Log.e("PlaceDetail", "Failed to get reviews for product ${product.name}: ${reviewResponse.message()}")
                    }
                }

                Log.d("PlaceDetail", "Final calculation: totalRating=$totalRating, totalReviews=$totalReviews")
                if (totalReviews > 0) {
                    val averageRating = (totalRating / totalReviews * 10.0).roundToInt() / 10.0
                    Log.d("PlaceDetail", "Calculated average rating: $averageRating")
                    place.value?.let { currentPlace ->
                        val updatedPlace = currentPlace.copy(rating = averageRating.toString())
                        val response = placeRepository.updatePlace(placeId.value ?: return@launch, updatedPlace)
                        if (response.isSuccessful) {
                            place.postValue(updatedPlace)
                            Log.d("PlaceDetail", "Place rating updated successfully: $averageRating")
                        } else {
                            Log.e("PlaceDetail", "Failed to update place rating: ${response.message()}")
                        }
                    }
                } else {
                    Log.d("PlaceDetail", "No reviews found for any products")
                }
            } catch (e: Exception) {
                Log.e("PlaceDetail", "Error updating place rating: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
