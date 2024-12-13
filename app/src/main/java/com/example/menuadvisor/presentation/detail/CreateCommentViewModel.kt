package com.example.menuadvisor.presentation.detail

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menuadvisor.model.ReviewRequest
import com.example.menuadvisor.repository.PlaceRepository
import com.example.menuadvisor.repository.ProductRepository
import com.example.menuadvisor.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.Context
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.model.ProductData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class CreateCommentViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _placeResponse = MutableStateFlow<ApiResponse<List<PlaceData>>?>(null)
    val placeResponse: StateFlow<ApiResponse<List<PlaceData>>?> = _placeResponse

    private val _productResponse = MutableStateFlow<ApiResponse<List<ProductData>>?>(null)
    val productResponse: StateFlow<ApiResponse<List<ProductData>>?> = _productResponse

    private val _reviewResponse = MutableStateFlow<ApiResponse<Int>?>(null)
    val reviewResponse: StateFlow<ApiResponse<Int>?> = _reviewResponse

    private val _selectedImage = MutableStateFlow<Uri?>(null)
    val selectedImage: StateFlow<Uri?> = _selectedImage

    private val _initialImage = MutableStateFlow<String?>(null)
    val initialImage: StateFlow<String?> = _initialImage

    var placeId = MutableLiveData<Int>()
    var placeName = MutableLiveData<String>()
    var productId = mutableStateOf<Int?>(null)
    var image  = MutableLiveData<String>()
    var imageUri = MutableLiveData<Uri>()

    init {
        viewModelScope.launch {
            Log.d("CreateCommentDebug", "ViewModel initialized")
        }
    }

    fun searchPlace(
        name : String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            placeRepository.getPlaces(name = name).let {
                if (it.isSuccessful) {
                    _placeResponse.value = it.body()
                } else {
                    _placeResponse.value = ApiResponse(
                        data = null,
                        errors = null,
                        message = it.errorBody()?.string(),
                        succeeded = false
                    )
                }
            }
            _isLoading.value = false
        }
    }

    fun searchProducts(
        name: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            productRepository.getProducts(name = name).let {
                if (it.isSuccessful) {
                    _productResponse.value = it.body()
                } else {
                    _productResponse.value = ApiResponse(
                        data = null,
                        errors = null,
                        message = it.errorBody()?.string(),
                        succeeded = false
                    )
                }
            }
            _isLoading.value = false
        }
    }

    fun getProductByPlaceId(
        placeId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            productRepository.getProductsByPlaceId(placeId).let {
                if (it.isSuccessful) {
                    _productResponse.value = it.body()
                } else {
                    _productResponse.value = ApiResponse(
                        data = null,
                        errors = null,
                        message = it.errorBody()?.string(),
                        succeeded = false
                    )
                }
            }
            _isLoading.value = false
        }
    }

    fun addProduct(
        name: String,
        description: String,
        image: String,
        rate: Int,
        price: Int,
        placeId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = productRepository.addProduct(name, description, image, rate, price, placeId)
            if (response.isSuccessful) {
                productId.value = response.body()?.data as? Int
            }
            _isLoading.value = false
        }
    }

    fun updateReview(reviewId: Int, reviewRequest: ReviewRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("CreateCommentDebug", "ReviewId: $reviewId")
                Log.d("CreateCommentDebug", "ReviewRequest: description=${reviewRequest.description}, rate=${reviewRequest.rate}, productId=${reviewRequest.productId}")
                
                val response = reviewRepository.updateReview(reviewId, reviewRequest)
                if (response.isSuccessful) {
                    Log.d("CreateCommentDebug", "Review update successful")
                    _reviewResponse.value = ApiResponse(
                        data = reviewId,
                        errors = null,
                        message = "Yorum başarıyla güncellendi",
                        succeeded = true
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateCommentDebug", "Review update failed with status ${response.code()}")
                    Log.e("CreateCommentDebug", "Error body: $errorBody")
                    _reviewResponse.value = ApiResponse(
                        data = null,
                        errors = null,
                        message = errorBody,
                        succeeded = false
                    )
                }
            } catch (e: Exception) {
                Log.e("CreateCommentDebug", "Error updating review", e)
                _reviewResponse.value = ApiResponse(
                    data = null,
                    errors = null,
                    message = "Yorum güncelleme sırasında hata oluştu: ${e.message}",
                    succeeded = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postReview(reviewRequest: ReviewRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("CreateCommentDebug", "Sending ReviewRequest: $reviewRequest")
                val response = reviewRepository.postReview(reviewRequest)
                if (response.isSuccessful) {
                    Log.d("CreateCommentDebug", "Review post successful: ${response.body()}")
                    _reviewResponse.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateCommentDebug", "Review post failed with status ${response.code()}")
                    Log.e("CreateCommentDebug", "Error body: $errorBody")
                    _reviewResponse.value = ApiResponse(
                        data = null,
                        errors = null,
                        message = errorBody,
                        succeeded = false
                    )
                }
            } catch (e: Exception) {
                Log.e("CreateCommentDebug", "Error posting review", e)
                _reviewResponse.value = ApiResponse(
                    data = null,
                    errors = null,
                    message = "Yorum gönderme sırasında hata oluştu: ${e.message}",
                    succeeded = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedImage(uri: Uri?) {
        _selectedImage.value = uri
        if (uri == null) {
            // Eğer resim kaldırıldıysa, initial image'ı da temizle
            _initialImage.value = null
        }
    }

    fun setInitialImage(imageBase64: String?) {
        _initialImage.value = imageBase64
        Log.d("CreateCommentDebug", "Initial image set: $imageBase64")
    }

    fun getBase64FromUri(uri: Uri, contentResolver: ContentResolver): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun submitReview(
        rating: Int,
        comment: String,
        imageUri: Uri?,
        contentResolver: ContentResolver,
        reviewId: Int? = null,
        isEdit: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Resim kontrolü
                val imageBase64 = when {
                    imageUri != null -> getBase64FromUri(imageUri, contentResolver) // Yeni resim seçildi
                    _initialImage.value != null -> _initialImage.value // Mevcut resim korundu
                    else -> null // Resim yok veya silindi
                }

                val reviewRequest = ReviewRequest(
                    productId = productId.value ?: 0,
                    rate = rating,
                    id = reviewId ?: 0,
                    description = comment,
                    image = imageBase64
                )

                Log.d("CreateCommentDebug", "Submitting review with image: ${imageBase64?.take(100)}")
                Log.d("CreateCommentDebug", "ReviewRequest: description=${reviewRequest.description}, rate=${reviewRequest.rate}, productId=${reviewRequest.productId}")

                val response = if (isEdit && reviewId != null) {
                    reviewRepository.updateReview(reviewId, reviewRequest)
                } else {
                    reviewRepository.postReview(reviewRequest)
                }

                if (response.isSuccessful) {
                    _reviewResponse.value = ApiResponse(
                        succeeded = true,
                        message = if (isEdit) "Yorum başarıyla güncellendi" else "Yorum başarıyla eklendi",
                        data = response.body()?.data,
                        errors = null
                    )
                } else {
                    _reviewResponse.value = ApiResponse(
                        succeeded = false,
                        message = "Bir hata oluştu: ${response.message()}",
                        data = null,
                        errors = null
                    )
                }
            } catch (e: Exception) {
                _reviewResponse.value = ApiResponse(
                    succeeded = false,
                    message = "Bir hata oluştu: ${e.message}",
                    data = null,
                    errors = null
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uriToBase64(context: Context, uri: Uri): String {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        inputStream?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while (it.read(buffer).also { len = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, len)
            }
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        return null.toString()
    }

    fun base64ToUri(context: Context, base64String: String, fileName: String): Uri {

        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        val imageFile = File(context.cacheDir, fileName)

        FileOutputStream(imageFile).use {
            it.write(decodedBytes)
            it.flush()
        }
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            imageFile
        )
    }

}