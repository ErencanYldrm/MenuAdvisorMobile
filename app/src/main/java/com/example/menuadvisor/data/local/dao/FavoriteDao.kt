package com.example.menuadvisor.data.local.dao

import androidx.room.*
import com.example.menuadvisor.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesByUserId(userId: String): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND placeId = :placeId")
    suspend fun deleteFavorite(userId: String, placeId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND placeId = :placeId)")
    fun isFavorite(userId: String, placeId: Int): Flow<Boolean>
}