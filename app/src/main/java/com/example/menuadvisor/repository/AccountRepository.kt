package com.example.menuadvisor.repository

import com.example.menuadvisor.api.AccountService
import com.example.menuadvisor.model.RegisterRequest
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountService: AccountService
) {
    suspend fun getUser(userId: String) = accountService.getUser(userId)
    
    suspend fun register(request: RegisterRequest) = accountService.register(request)
    
    suspend fun confirmEmail(userId: String, code: String) = accountService.confirmEmail(userId, code)
}
