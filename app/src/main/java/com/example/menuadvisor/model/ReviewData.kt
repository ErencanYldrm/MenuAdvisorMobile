package com.example.menuadvisor.model


import com.google.gson.annotations.SerializedName

data class ReviewData(
    @SerializedName("created")
    val created: String?,
    @SerializedName("createdBy")
    val createdBy: Any?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("price")
    val price: Int?,
    @SerializedName("productId")
    val productId: Int?,
    @SerializedName("rate")
    val rate: Int?,
    @SerializedName("userId")
    val userId: Any?
)