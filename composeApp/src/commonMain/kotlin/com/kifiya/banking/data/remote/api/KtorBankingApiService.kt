package com.kifiya.banking.data.remote.api

import com.kifiya.banking.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorBankingApiService(private val client: HttpClient) : BankingApiService {

    private val baseUrl = "https://challenge-api.qena.dev"

    override suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun register(request: RegisterRequest): UserDto {
        return client.post("$baseUrl/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): LoginResponse {
        return client.post("$baseUrl/api/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getAccounts(page: Int, size: Int, accessToken: String): PaginatedResponse<AccountDto> {
        return client.get("$baseUrl/api/accounts") {
            parameter("page", page)
            parameter("size", size)
            bearerAuth(accessToken)
        }.body()
    }

    override suspend fun getAccountById(accountId: Long, accessToken: String): AccountDto {
        return client.get("$baseUrl/api/accounts/$accountId") {
            bearerAuth(accessToken)
        }.body()
    }

    override suspend fun createAccount(request: CreateAccountRequest, accessToken: String): AccountDto {
        return client.post("$baseUrl/api/accounts") {
            contentType(ContentType.Application.Json)
            bearerAuth(accessToken)
            setBody(request)
        }.body()
    }

    override suspend fun transfer(request: TransferRequest, accessToken: String) {
        client.post("$baseUrl/api/accounts/transfer") {
            contentType(ContentType.Application.Json)
            bearerAuth(accessToken)
            setBody(request)
        }
    }

    override suspend fun getTransactions(
        accountId: Long,
        page: Int,
        size: Int,
        accessToken: String
    ): PaginatedResponse<TransactionDto> {
        return client.get("$baseUrl/api/transactions/$accountId") {
            parameter("page", page)
            parameter("size", size)
            bearerAuth(accessToken)
        }.body()
    }
}

