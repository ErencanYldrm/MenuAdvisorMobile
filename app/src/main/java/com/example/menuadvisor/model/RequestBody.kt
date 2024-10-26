package com.example.menuadvisor.model

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val userName: String,
    val password: String,
    val confirmPassword: String
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class ConfirmEmailRequest(
    val userId: String,
    val code: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val password: String,
    val confirmPassword: String
)

data class ReviewRequest(
    val description: String,
    val rate: Int,
    val image: String? = null,
    val price: Int? = null,
    val productId: Int,
    val userId: String,
)

data class ProductRequest(
    val name: String,
    val description: String = "",
    val image: String = "",
    val price: Int = 0,
    val placeId: Int,
    val rate: Int = 0
)