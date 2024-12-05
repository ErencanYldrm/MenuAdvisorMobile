package com.example.menuadvisor.model


import com.google.gson.annotations.SerializedName

data class ProductData(
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("placeId")
    val placeId: Int?,
    @SerializedName("price")
    val price: Int?,
    @SerializedName("rate")
    val rate: Double?
)