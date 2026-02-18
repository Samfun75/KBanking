package com.kifiya.banking.domain.repository

import com.kifiya.banking.domain.model.Account

interface AccountRepository {
    suspend fun getAccounts(page: Int, size: Int): Result<List<Account>>
    suspend fun getAccountById(accountId: Long): Result<Account>
    suspend fun createAccount(accountType: String): Result<Account>
    suspend fun transfer(
        fromAccountNumber: String,
        toAccountNumber: String,
        amount: Double
    ): Result<Unit>
    fun getCachedAccounts(): List<Account>
    fun invalidateCache()
}

