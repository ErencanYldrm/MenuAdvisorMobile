package com.example.menuadvisor.model


import com.google.gson.annotations.SerializedName

data class AuthData(
    @SerializedName("email")
    val email: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isVerified")
    val isVerified: Boolean?,
    @SerializedName("jwToken")
    val jwToken: String?,
    @SerializedName("roles")
    val roles: List<String?>?,
    @SerializedName("userName")
    val userName: String?
)