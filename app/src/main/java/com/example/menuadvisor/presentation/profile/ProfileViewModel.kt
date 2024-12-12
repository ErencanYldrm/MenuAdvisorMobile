package com.example.menuadvisor.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.ReviewData
import com.example.menuadvisor.repository.PlaceRepository
import com.example.menuadvisor.repository.ProductRepository
import com.example.menuadvisor.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val placeRepository: PlaceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _commentCount = MutableStateFlow(0)
    val commentCount: StateFlow<Int> = _commentCount

    private val _userReviews = MutableStateFlow<List<ReviewData>>(emptyList())
    val userReviews: StateFlow<List<ReviewData>> = _userReviews

    // Ürün ID'lerine göre ürün isimlerini tutacak map
    private val _productNames = MutableStateFlow<Map<Int, String>>(emptyMap())
    val productNames: StateFlow<Map<Int, String>> = _productNames

    // Ürün ID'lerine göre restoran isimlerini tutacak map
    private val _placeNames = MutableStateFlow<Map<Int, String>>(emptyMap())
    val placeNames: StateFlow<Map<Int, String>> = _placeNames

    init {
        getUserCommentCount()
    }

    fun getUserCommentCount() {
        viewModelScope.launch {
            try {
                val userId = userPreferences.userId.first()
                if (userId.isNullOrEmpty()) {
                    Log.e("ProfileViewModel", "UserId is null or empty")
                    return@launch
                }

                val response = reviewRepository.getAllReviews()
                if (response.isSuccessful) {
                    val reviews = response.body()?.data ?: emptyList()
                    // Kullanıcının yorumlarını userId'ye göre filtrele
                    val userReviews = reviews.filter { it.createdBy == userId }
                    Log.d("ProfileViewModel", "Found ${userReviews.size} reviews for user $userId")
                    _commentCount.value = userReviews.size
                    _userReviews.value = userReviews

                    // Her yorum için ürün bilgisini al
                    userReviews.forEach { review ->
                        review.productId?.let { productId ->
                            getProductName(productId)
                        }
                    }
                } else {
                    Log.e("ProfileViewModel", "Failed to get reviews: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error getting user comments: ${e.message}")
            }
        }
    }

    private fun getProductName(productId: Int) {
        viewModelScope.launch {
            try {
                val response = productRepository.getProduct(productId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { product ->
                        // Ürün adını kaydet
                        product.name?.let { name ->
                            _productNames.value = _productNames.value + (productId to name)
                        }
                        // Ürünün ait olduğu restoranın adını al
                        product.placeId?.let { placeId ->
                            getPlaceName(productId, placeId)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error getting product name: ${e.message}")
            }
        }
    }

    private fun getPlaceName(productId: Int, placeId: Int) {
        viewModelScope.launch {
            try {
                val response = placeRepository.getPlace(placeId)
                if (response.isSuccessful) {
                    response.body()?.data?.name?.let { name ->
                        _placeNames.value = _placeNames.value + (productId to name)
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error getting place name: ${e.message}")
            }
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            try {
                val response = reviewRepository.deleteReview(reviewId)
                if (response.isSuccessful) {
                    // Yorum silindikten sonra listeyi güncelle
                    getUserCommentCount()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error deleting review: ${e.message}")
            }
        }
    }
} 