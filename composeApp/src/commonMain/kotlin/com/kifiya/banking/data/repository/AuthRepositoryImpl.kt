package com.kifiya.banking.data.repository

import com.kifiya.banking.data.local.TokenStorage
import com.kifiya.banking.data.mapper.toAuthTokens
import com.kifiya.banking.data.mapper.toUser
import com.kifiya.banking.data.remote.api.ApiException
import com.kifiya.banking.data.remote.api.BankingApiService
import com.kifiya.banking.data.remote.dto.LoginRequest
import com.kifiya.banking.data.remote.dto.RefreshTokenRequest
import com.kifiya.banking.data.remote.dto.RegisterRequest
import com.kifiya.banking.domain.model.AuthTokens
import com.kifiya.banking.domain.model.User
import com.kifiya.banking.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: BankingApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthTokens> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            Result.success(response.toAuthTokens())
        } catch (e: ApiException) {
            Result.failure(Exception(e.message))
        } catch (e: Exception) {
            Result.failure(Exception("Network error. Please check your connection."))
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ): Result<User> {
        return try {
            val response = apiService.register(
                RegisterRequest(
                    username = username,
                    passwordHash = password,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber
                )
            )
            Result.success(response.toUser())
        } catch (e: ApiException) {
            Result.failure(Exception(e.message))
        } catch (e: Exception) {
            Result.failure(Exception("Network error. Please check your connection."))
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthTokens> {
        return try {
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            Result.success(response.toAuthTokens())
        } catch (e: ApiException) {
            Result.failure(Exception(e.message))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to refresh session. Please login again."))
        }
    }

    override suspend fun logout() {
        clearTokens()
    }

    override suspend fun getStoredTokens(): AuthTokens? {
        return tokenStorage.getTokens()
    }

    override suspend fun saveTokens(tokens: AuthTokens) {
        tokenStorage.saveTokens(tokens)
    }

    override suspend fun clearTokens() {
        tokenStorage.clearTokens()
    }

    override fun isLoggedIn(): Boolean {
        return tokenStorage.isLoggedIn()
    }
}

