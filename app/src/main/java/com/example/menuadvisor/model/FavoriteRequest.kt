package com.example.menuadvisor.model

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("placeId")
    val placeId: Int
) 