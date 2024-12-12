package com.example.menuadvisor.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.model.ReviewData
import com.example.menuadvisor.repository.ProductRepository
import com.example.menuadvisor.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
//    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    val productId = MutableLiveData<Int>()
    val product = MutableLiveData<ProductData>()
    val commentList = MutableLiveData<List<ReviewData>>()


//    val commentList = MutableLiveData<List<ReviewData>>()
//
//    suspend fun getCommentsByProductId() {
//        productId.value?.let {
//            reviewRepository.getReviewsByProductId(it).let {
//                if (it.isSuccessful) {
//                    commentList.value = it.body()?.data!!
//                }
//            }
//        }
//    }

//    fun getCommentsByRateNumber(rateNumber: Int) = viewModelScope.launch{
//        productId.value?.let {
//            reviewRepository.getReviewsByRateNumber(rateNumber, it).let {
//                if (it.isSuccessful) {
//                    commentList.value = it.body()?.data!!
//                }
//            }
//        }
//    }

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
            reviewRepository.getReviewsByProductId(it).let {
                if (it.isSuccessful) {
                    commentList.value = it.body()?.data as List<ReviewData>?
                }
            }
        }
    }


}