package com.example.menuadvisor.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("succeeded")
    val succeeded: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("errors")
    val errors: Any?,
    @SerializedName("data")
    val data: String // userId
)
