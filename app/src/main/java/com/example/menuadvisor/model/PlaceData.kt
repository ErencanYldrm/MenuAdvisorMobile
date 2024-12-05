package com.example.menuadvisor.model


import com.google.gson.annotations.SerializedName

data class PlaceData(
    @SerializedName("address")
    val address: String?,
    @SerializedName("created")
    val created: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("district")
    val district: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("lastModified")
    val lastModified: String?,
    @SerializedName("lastModifiedBy")
    val lastModifiedBy: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("lon")
    val lon: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("products")
    val products: List<String?>?,
    @SerializedName("types")
    val types: List<String?>?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("workingHours")
    val workingHours: String?
)