package com.kifiya.banking.domain.repository

import com.kifiya.banking.domain.model.AuthTokens
import com.kifiya.banking.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthTokens>
    suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ): Result<User>
    suspend fun refreshToken(refreshToken: String): Result<AuthTokens>
    suspend fun logout()
    suspend fun getStoredTokens(): AuthTokens?
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clearTokens()
    fun isLoggedIn(): Boolean
}

