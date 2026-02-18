package com.kifiya.banking.data.remote.api

import com.kifiya.banking.data.remote.dto.*

interface BankingApiService {
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun register(request: RegisterRequest): UserDto
    suspend fun refreshToken(request: RefreshTokenRequest): LoginResponse

    suspend fun getAccounts(page: Int, size: Int, accessToken: String): PaginatedResponse<AccountDto>
    suspend fun getAccountById(accountId: Long, accessToken: String): AccountDto
    suspend fun createAccount(request: CreateAccountRequest, accessToken: String): AccountDto
    suspend fun transfer(request: TransferRequest, accessToken: String)

    suspend fun getTransactions(accountId: Long, page: Int, size: Int, accessToken: String): PaginatedResponse<TransactionDto>
}

