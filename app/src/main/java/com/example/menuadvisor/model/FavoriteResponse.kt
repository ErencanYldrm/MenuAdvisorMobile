package com.example.menuadvisor.model

import com.google.gson.annotations.SerializedName
import com.example.menuadvisor.model.PlaceData

data class FavoriteResponse(
    @SerializedName("pageNumber")
    val pageNumber: Int?,
    @SerializedName("pageSize")
    val pageSize: Int?,
    @SerializedName("succeeded")
    val succeeded: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("errors")
    val errors: String?,
    @SerializedName("data")
    val data: FavoriteData?
)

data class FavoriteData(
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("places")
    val places: List<PlaceData>?
)