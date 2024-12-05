package com.example.menuadvisor.presentation.detail_screens.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.repository.PlaceRepository
import com.example.menuadvisor.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// PlaceDetailViewModel.kt
@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    val placeId = MutableLiveData<Int>()
    val place = MutableLiveData<PlaceData>()
    val productList = MutableLiveData<List<ProductData>>()

    fun getPlace() {
        placeId.value?.let {
            viewModelScope.launch {
                val response = placeRepository.getPlace(it)
                if (response.isSuccessful) {
                    place.postValue(response.body()?.data)
                }
            }
        }
    }

    fun getProductsByPlaceId() {
        placeId.value?.let {
            viewModelScope.launch {
                val response = productRepository.getProductsByPlaceId(it)
                if (response.isSuccessful) {
                    productList.postValue(response.body()?.data)
                }
            }
        }
    }
}


