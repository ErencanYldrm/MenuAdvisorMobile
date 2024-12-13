package com.example.menuadvisor.presentation.home

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.ApiResponse
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
class HomeViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val productRepository: ProductRepository,
    private val userPreferences: UserPreferences,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    val token = MutableLiveData<String?>()
    private val _allPlaces = MutableStateFlow<List<PlaceData>>(listOf())
    val allPlaces: StateFlow<List<PlaceData>> = _allPlaces

    private val _allProducts = MutableStateFlow<List<ProductData>>(listOf())
    val allProducts: StateFlow<List<ProductData>> = _allProducts

    private val _placeNames = MutableStateFlow<Map<Int, String>>(mapOf())
    val placeNames: StateFlow<Map<Int, String>> = _placeNames

    private val _placeReviewCounts = MutableStateFlow<Map<Int, Int>>(mapOf())
    val placeReviewCounts: StateFlow<Map<Int, Int>> = _placeReviewCounts

    private val _productReviewCounts = MutableStateFlow<Map<Int, Int>>(mapOf())
    val productReviewCounts: StateFlow<Map<Int, Int>> = _productReviewCounts

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    private val _placeDistances = MutableStateFlow<Map<Int, Float>>(mapOf())
    val placeDistances: StateFlow<Map<Int, Float>> = _placeDistances

    init {
        viewModelScope.launch {
            userPreferences.userToken.collect { savedToken ->
                token.postValue(savedToken)
            }
        }
        getAllPlaces()
        getAllProducts()
    }

    fun updateUserLocation(context: Context) {
        viewModelScope.launch {
            try {
                val location = LocationUtils.getCurrentLocation(context)
                _userLocation.value = location
                // Kullanıcının konumu güncellendiğinde, tüm mekanların mesafelerini güncelle
                updatePlaceDistances()
            } catch (e: Exception) {
                // Hata durumunda sessizce devam et
            }
        }
    }

    private fun updatePlaceDistances() {
        try {
            val currentLocation = _userLocation.value ?: return
            val newDistances = mutableMapOf<Int, Float>()
            
            _allPlaces.value.forEach { place ->
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
                    // Tek bir mekan için hata olursa diğerlerine devam et
                }
            }
            _placeDistances.value = newDistances
        } catch (e: Exception) {
            // Genel hata durumunda sessizce devam et
        }
    }

    fun getAllPlaces() {
        viewModelScope.launch {
            val response = placeRepository.getPlaces()
            if (response.isSuccessful) {
                response.body()?.data?.let { places ->
                    _allPlaces.value = places
                    // Her mekan için yorum sayısını al
                    places.forEach { place ->
                        place.id?.let { placeId ->
                            getPlaceReviewCount(placeId)
                        }
                    }
                }
            }
        }
    }

    fun getAllProducts() {
        viewModelScope.launch {
            try {
                val response = productRepository.getProducts(name = "", pageSize = 15)
                if (response.isSuccessful) {
                    response.body()?.data?.let { products ->
                        _allProducts.value = products
                        // Ürünlerin mekan bilgilerini ve yorum sayılarını al
                        products.forEach { product ->
                            product.placeId?.let { placeId ->
                                getPlaceName(placeId)
                            }
                            product.id?.let { productId ->
                                getProductReviewCount(productId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun getPlaceName(placeId: Int) {
        viewModelScope.launch {
            try {
                val response = placeRepository.getPlace(placeId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { place ->
                        _placeNames.value = _placeNames.value + (placeId to (place.name ?: ""))
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
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
}