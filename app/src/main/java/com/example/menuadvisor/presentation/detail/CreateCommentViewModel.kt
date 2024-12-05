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

    var placeId = MutableLiveData<Int>()
    var placeName = MutableLiveData<String>()
    var productId = MutableLiveData<Int?>()
    var image  = MutableLiveData<String>()
    var imageUri = MutableLiveData<Uri>()
    var userId = MutableLiveData<String?>()
    var userName = MutableLiveData<String?>()

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { savedUserId ->
                Log.d("CreateCommentDebug", "UserPreferences - UserId: $savedUserId")
                userId.postValue(savedUserId)
            }
        }
        
        viewModelScope.launch {
            userPreferences.userName.collect { savedUserName ->
                Log.d("CreateCommentDebug", "UserPreferences - UserName: $savedUserName")
                userName.postValue(savedUserName)
            }
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
                productId.value = response.body()?.data
            }
            _isLoading.value = false
        }
    }

    suspend fun postReview(reviewRequest: ReviewRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("CreateCommentDebug", "Sending ReviewRequest: $reviewRequest")
                val response = reviewRepository.postReview(reviewRequest)
                if (response.isSuccessful) {
                    Log.d("CreateCommentDebug", "Review post successful: ${response.body()}")
                    _reviewResponse.value = response.body()
                } else {
                    Log.d("CreateCommentDebug", "Review post failed: ${response.errorBody()?.string()}")
                    _reviewResponse.value = ApiResponse(
                        data = null,
                        errors = null,
                        message = response.errorBody()?.string(),
                        succeeded = false
                    )
                }
            } catch (e: Exception) {
                Log.e("CreateCommentDebug", "Error posting review", e)
                _reviewResponse.value = ApiResponse(
                    data = null,
                    errors = null,
                    message = e.message,
                    succeeded = false
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