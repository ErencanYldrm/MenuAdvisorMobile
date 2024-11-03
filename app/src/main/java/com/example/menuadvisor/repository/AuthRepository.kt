package com.example.menuadvisor.repository

import com.example.menuadvisor.api.AuthService
import com.example.menuadvisor.model.AuthRequest
import com.example.menuadvisor.model.RegisterRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        userName: String,
        password: String,
        confirmPassword: String
    ) =
        authService.register(
            RegisterRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                userName = userName,
                password = password,
                confirmPassword = confirmPassword
            )
        )


    suspend fun authenticate(email: String, password: String) =
        authService.authenticate(
            AuthRequest(
                email = email,
                password = password
            )
        )

    suspend fun info() =
        authService.info()



//    suspend fun confirmEmail(userId: String, code: String) =
//        authService.confirmEmail(userId, code)
//
//    suspend fun forgotPassword(email: String) =
//        authService.forgotPassword(
//            ForgotPasswordRequest(
//                email = email
//            )
//        )
//
//    suspend fun resetPassword(email: String, token: String, password: String, confirmPassword: String) =
//        authService.resetPassword(
//            ResetPasswordRequest(
//                email = email,
//                token = token,
//                password = password,
//                confirmPassword = confirmPassword
//            )
//        )

}