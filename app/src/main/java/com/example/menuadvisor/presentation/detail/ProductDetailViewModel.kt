package com.example.menuadvisor.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.model.ReviewData
import com.example.menuadvisor.model.UserData
import com.example.menuadvisor.repository.AccountRepository
import com.example.menuadvisor.repository.ProductRepository
import com.example.menuadvisor.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val accountRepository: AccountRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val productId = MutableLiveData<Int>()
    val product = MutableLiveData<ProductData>()
    val commentList = MutableLiveData<List<ReviewData>>()

    private val _reviews = MutableStateFlow<List<ReviewData>>(emptyList())
    val reviews: StateFlow<List<ReviewData>> = _reviews

    private val _userNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val userNames: StateFlow<Map<String, String>> = _userNames

    suspend fun getProduct() {
        productId.value?.let {
            productRepository.getProduct(productId.value!!).let {
                if (it.isSuccessful) {
                    product.value = it.body()?.data as ProductData?
                }
            }
        }
    }

    suspend fun getCommentsByProductId() {
        productId.value?.let {
            reviewRepository.getReviewsByProductId(it).let { response ->
                if (response.isSuccessful) {
                    val reviews = response.body()?.data ?: emptyList()
                    _reviews.value = reviews
                    commentList.value = reviews

                    // Her yorum için kullanıcı bilgisini al
                    reviews.forEach { review ->
                        review.createdBy?.toString()?.let { userId ->
                            getUserName(userId)
                        }
                    }
                }
            }
        }
    }

    private fun getUserName(userId: String) {
        viewModelScope.launch {
            try {
                val response = accountRepository.getUser(userId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { user ->
                        val displayName = "${user.firstName} ${user.lastName}"
                        _userNames.value = _userNames.value + (userId to displayName)
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductDetailViewModel", "Error getting user name: ${e.message}")
            }
        }
    }
}