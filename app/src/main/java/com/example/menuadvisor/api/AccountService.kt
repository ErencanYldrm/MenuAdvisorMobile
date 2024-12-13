package com.example.menuadvisor.api

import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.UserData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AccountService {
    @GET("api/Account/get-user")
    suspend fun getUser(@Query("userId") userId: String): Response<ApiResponse<UserData>>
}
