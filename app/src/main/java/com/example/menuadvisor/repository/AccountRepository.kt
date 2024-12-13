package com.example.menuadvisor.repository

import com.example.menuadvisor.api.AccountService
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountService: AccountService
) {
    suspend fun getUser(userId: String) = accountService.getUser(userId)
}
