package com.example.menuadvisor.data.local.dao

import androidx.room.*
import com.example.menuadvisor.data.local.entity.ProductFavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductFavoriteDao {
    @Query("SELECT * FROM product_favorites WHERE userId = :userId")
    fun getFavoritesByUserId(userId: String): Flow<List<ProductFavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: ProductFavoriteEntity)

    @Query("DELETE FROM product_favorites WHERE userId = :userId AND productId = :productId")
    suspend fun deleteFavorite(userId: String, productId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM product_favorites WHERE userId = :userId AND productId = :productId)")
    fun isFavorite(userId: String, productId: Int): Flow<Boolean>
} 