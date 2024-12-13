package com.example.menuadvisor.api

import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.RegisterRequest
import com.example.menuadvisor.model.RegisterResponse
import com.example.menuadvisor.model.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AccountService {
    @GET("api/Account/get-user")
    suspend fun getUser(@Query("userId") userId: String): Response<ApiResponse<UserData>>

    @POST("api/Account/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/Account/confirm-email")
    suspend fun confirmEmail(
        @Query("userId") userId: String,
        @Query("code") code: String
    ): Response<ApiResponse<Unit>>
}
