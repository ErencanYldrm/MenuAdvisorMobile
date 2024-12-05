package com.example.menuadvisor.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userPreferences: UserPreferences
) :
    ViewModel() {

    val token = MutableLiveData<String?>()

    init {
        viewModelScope.launch {
            userPreferences.userToken.collect { savedToken ->
                token.postValue(savedToken)
            }
        }
        getAllPlaces()
    }

    private val _placesResponse = MutableStateFlow<ApiResponse<List<PlaceData>>?>(null)
    val placesResponse: StateFlow<ApiResponse<List<PlaceData>>?> = _placesResponse

    private val _places = MutableStateFlow<List<PlaceData>>(listOf())
    val places: StateFlow<List<PlaceData>> = _places

    private val _favPlaces = MutableStateFlow<List<PlaceData>>(listOf())
    val favPlaces: StateFlow<List<PlaceData>> = _favPlaces

    private val _allPlaces = MutableStateFlow<List<PlaceData>>(listOf())
    val allPlaces: StateFlow<List<PlaceData>> = _allPlaces

    private val _searchList = MutableStateFlow<List<PlaceData>>(listOf())
    val searchList: StateFlow<List<PlaceData>> = _searchList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var district = MutableStateFlow("Kepez")

    private var pageNumber: Int = 1
    private var pageSize: Int = 10

    var searchQuery = MutableStateFlow("")

    fun getPlaces(
        pageNumber: Int? = 1,
        pageSize: Int? = 10,
        district : String? = null,
        rateSort : Boolean? = false
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val response = placeRepository.getPlaces(
                pageNumber = pageNumber,
                pageSize = pageSize,
                district = district,
                rateSort = rateSort
            )
            if (response.isSuccessful) {
                _placesResponse.value = response.body()
                response.body()?.data?.let {
                    _places.value = it
                }
            } else {
                _placesResponse.value = ApiResponse(
                    data = null,
                    errors = null,
                    message = response.errorBody()?.string(),
                    succeeded = false
                )
            }
            _isLoading.value = false
        }
    }

    fun getAllPlaces() {
        viewModelScope.launch {
            val response = placeRepository.getPlaces(pageSize = 15)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _allPlaces.value = it
                }
            }
        }
    }

    fun getFavPlaces(district: String? = this.district.value) {
        viewModelScope.launch {
            val response = placeRepository.getPlaces(district = district, rateSort = true)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _favPlaces.value = it
                }
            }
        }
    }

    fun loadMore() {
        pageNumber += 1
        viewModelScope.launch {
            val response = placeRepository.getPlaces(
                pageNumber = pageNumber,
                pageSize = pageSize
            )
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _places.value = _places.value + it
                }
            }
        }
    }

    fun searchPlace(name: String) {
        viewModelScope.launch {
            val response = placeRepository.getPlaces(name = name)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _searchList.value = it
                }
            }
        }
    }
}