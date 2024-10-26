package com.example.menuadvisor.api
//
import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.AuthData
import com.example.menuadvisor.model.AuthRequest
import com.example.menuadvisor.model.ForgotPasswordRequest
import com.example.menuadvisor.model.RegisterRequest
import com.example.menuadvisor.model.ResetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

    // Base URL: https://menu-advisor.azurewebsites.net/
    // Endpoint: api/Account/register

    @POST("api/Account/register")
    suspend fun register(
        @Body requestBody: RegisterRequest
    ): Response<ApiResponse<String>>

    // Endpoint: api/Account/authenticate
    @POST("api/Account/authenticate")
    suspend fun authenticate(
        @Body requestBody: AuthRequest
    ): Response<ApiResponse<AuthData>>



//    @GET("api/Account/confirm-email")
//    suspend fun confirmEmail(
//        @Query("userId") userId: String,
//        @Query("code") code: String
//    ): Response<ApiResponse<String>>
//
//    @POST("api/Account/reset-password")
//    suspend fun resetPassword(
//        @Body requestBody: ResetPasswordRequest
//    ): Response<ApiResponse<String>>
//
//    @POST("api/Account/forgot-password")
//    suspend fun forgotPassword(
//        @Body requestBody: ForgotPasswordRequest
//    ): Response<Void>


}