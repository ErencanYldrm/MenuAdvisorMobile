package com.example.menuadvisor.model


import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("data")
    val data :T?,
    @SerializedName("errors")
    val errors: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("succeeded")
    val succeeded: Boolean?
)