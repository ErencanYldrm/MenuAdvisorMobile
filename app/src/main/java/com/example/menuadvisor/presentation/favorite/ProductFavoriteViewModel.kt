package com.example.menuadvisor.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.local.dao.ProductFavoriteDao
import com.example.menuadvisor.data.local.entity.ProductFavoriteEntity
import com.example.menuadvisor.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductFavoriteViewModel @Inject constructor(
    private val productFavoriteDao: ProductFavoriteDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun getFavorites(userId: String): Flow<List<ProductFavoriteEntity>> {
        return productFavoriteDao.getFavoritesByUserId(userId)
    }

    fun isFavorite(userId: String, productId: Int): Flow<Boolean> {
        return productFavoriteDao.isFavorite(userId, productId)
    }

    fun addFavorite(userId: String, productId: Int) {
        viewModelScope.launch {
            val favorite = ProductFavoriteEntity(
                userId = userId,
                productId = productId
            )
            productFavoriteDao.insertFavorite(favorite)
        }
    }

    fun removeFavorite(userId: String, productId: Int) {
        viewModelScope.launch {
            productFavoriteDao.deleteFavorite(userId, productId)
        }
    }
} 