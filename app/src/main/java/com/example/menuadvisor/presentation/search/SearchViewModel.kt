package com.example.menuadvisor.presentation.search

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.repository.PlaceRepository
import com.example.menuadvisor.repository.ProductRepository
import com.example.menuadvisor.repository.ReviewRepository
import com.example.menuadvisor.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val token = MutableLiveData<String?>()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _places = MutableStateFlow<List<PlaceData>>(emptyList())
    val places: StateFlow<List<PlaceData>> = _places

    private val _products = MutableStateFlow<List<ProductData>>(emptyList())
    val products: StateFlow<List<ProductData>> = _products

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    private val _placeDistances = MutableStateFlow<Map<Int, Float>>(mapOf())
    val placeDistances: StateFlow<Map<Int, Float>> = _placeDistances

    private val _placeReviewCounts = MutableStateFlow<Map<Int, Int>>(mapOf())
    val placeReviewCounts: StateFlow<Map<Int, Int>> = _placeReviewCounts

    private val _productReviewCounts = MutableStateFlow<Map<Int, Int>>(mapOf())
    val productReviewCounts: StateFlow<Map<Int, Int>> = _productReviewCounts

    init {
        viewModelScope.launch {
            userPreferences.userToken.collect { savedToken ->
                token.postValue(savedToken)
            }
        }
    }

    fun updateUserLocation(context: Context) {
        viewModelScope.launch {
            try {
                val location = LocationUtils.getCurrentLocation(context)
                _userLocation.value = location
                updatePlaceDistances()
            } catch (e: Exception) {
                // Handle location error
            }
        }
    }

    private fun updatePlaceDistances() {
        try {
            val currentLocation = _userLocation.value ?: return
            val newDistances = mutableMapOf<Int, Float>()
            
            _places.value.forEach { place ->
                try {
                    place.id?.let { placeId ->
                        place.lat?.let { latStr ->
                            place.lon?.let { lonStr ->
                                val latitude = latStr.replace(",", ".").toDoubleOrNull()
                                val longitude = lonStr.replace(",", ".").toDoubleOrNull()
                                
                                if (latitude != null && longitude != null) {
                                    val distance = LocationUtils.calculateDistance(
                                        currentLocation.latitude,
                                        currentLocation.longitude,
                                        latitude,
                                        longitude
                                    )
                                    newDistances[placeId] = distance
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle single place error
                }
            }
            _placeDistances.value = newDistances
        } catch (e: Exception) {
            // Handle general error
        }
    }

    private fun getPlaceReviewCount(placeId: Int) {
        viewModelScope.launch {
            try {
                val response = reviewRepository.getReviewCountByPlaceId(placeId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { reviews ->
                        _placeReviewCounts.value = _placeReviewCounts.value + (placeId to reviews.size)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun getProductReviewCount(productId: Int) {
        viewModelScope.launch {
            try {
                val response = reviewRepository.getReviewsByProductId(productId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { reviews ->
                        _productReviewCounts.value = _productReviewCounts.value + (productId to reviews.size)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _places.value = emptyList()
            _products.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Search places
                val placesResponse = placeRepository.getPlaces(name = query)
                if (placesResponse.isSuccessful) {
                    _places.value = placesResponse.body()?.data ?: emptyList()
                    updatePlaceDistances()
                    // Get review counts for places
                    _places.value.forEach { place ->
                        place.id?.let { placeId ->
                            getPlaceReviewCount(placeId)
                        }
                    }
                }

                // Search products
                val productsResponse = productRepository.getProducts(name = query)
                if (productsResponse.isSuccessful) {
                    _products.value = productsResponse.body()?.data ?: emptyList()
                    // Get review counts for products
                    _products.value.forEach { product ->
                        product.id?.let { productId ->
                            getProductReviewCount(productId)
                        }
                    }
                }

            } catch (e: Exception) {
                _places.value = emptyList()
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
