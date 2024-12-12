package com.example.menuadvisor.model

import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("rate")
    val rate: Int,
    @SerializedName("image")
    val image: String? = "string",
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("productId")
    val productId: Int
)