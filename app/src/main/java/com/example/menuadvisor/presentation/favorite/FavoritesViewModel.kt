package com.example.menuadvisor.presentation.favorite

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    val favorites = MutableLiveData<List<PlaceData>?>()
    var userId = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun getFavorites() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                userId.value?.let { id ->
                    favoritesRepository.getFavorites(id).let {
                        if (it.isSuccessful) {
                            favorites.postValue(it.body()?.data)
                        } else {
                            Log.e("FavoritesViewModel", "Error: ${it.errorBody()?.string()}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFavorite(placeId: Int) {
        viewModelScope.launch {
            try {
                userId.value?.let { id ->
                    val response = favoritesRepository.addFavorite(id, placeId)
                    if (response.isSuccessful) {
                        getFavorites()
                    } else {
                        Log.e("FavoritesViewModel", "Error adding favorite: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Exception adding favorite: ${e.message}")
            }
        }
    }

    fun removeFavorite(id: Int) {
        viewModelScope.launch {
            try {
                val response = favoritesRepository.removeFavorite(id)
                if (response.isSuccessful) {
                    getFavorites()
                } else {
                    Log.e("FavoritesViewModel", "Error removing favorite: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Exception removing favorite: ${e.message}")
            }
        }
    }
}