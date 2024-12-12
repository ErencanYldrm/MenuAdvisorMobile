package com.example.menuadvisor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_favorites")
data class ProductFavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val productId: Int,
    val timestamp: Long = System.currentTimeMillis()
) 