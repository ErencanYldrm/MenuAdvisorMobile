package com.example.menuadvisor.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id")
    val id: String?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("email")
    val email: String?
)
