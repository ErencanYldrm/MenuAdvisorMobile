package com.example.menuadvisor.model

import com.google.gson.annotations.SerializedName

data class FavoriteRelation(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("placeId")
    val placeId: Int
) 