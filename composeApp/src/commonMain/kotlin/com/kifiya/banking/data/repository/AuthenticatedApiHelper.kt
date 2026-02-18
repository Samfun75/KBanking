package com.kifiya.banking.data.repository

import com.kifiya.banking.data.local.TokenStorage
import com.kifiya.banking.data.mapper.toAuthTokens
import com.kifiya.banking.data.remote.api.ApiException
import com.kifiya.banking.data.remote.api.BankingApiService
import com.kifiya.banking.data.remote.dto.RefreshTokenRequest

class AuthenticatedApiHelper(
    private val apiService: BankingApiService,
    private val tokenStorage: TokenStorage
) {
    suspend fun <T> executeWithTokenRefresh(
        apiCall: suspend (accessToken: String) -> T
    ): Result<T> {
        var token = tokenStorage.getAccessTokenOrNull()
            ?: return Result.failure(Exception("Not authenticated"))

        // Check if token is about to expire or expired, refresh proactively
        if (tokenStorage.isTokenExpired()) {
            val refreshResult = refreshToken()
            if (refreshResult.isFailure) {
                return Result.failure(UnauthorizedException("Session expired. Please login again."))
            }
            token = tokenStorage.getAccessTokenOrNull()
                ?: return Result.failure(Exception("Not authenticated"))
        }

        return try {
            Result.success(apiCall(token))
        } catch (e: ApiException) {
            if (e.code == 401) {
                // Token might have just expired, try refresh once
                val refreshResult = refreshToken()
                if (refreshResult.isSuccess) {
                    val newToken = tokenStorage.getAccessTokenOrNull()
                        ?: return Result.failure(UnauthorizedException("Session expired"))
                    try {
                        Result.success(apiCall(newToken))
                    } catch (retryException: ApiException) {
                        if (retryException.code == 401) {
                            tokenStorage.clearTokens()
                            Result.failure(UnauthorizedException("Session expired. Please login again."))
                        } else {
                            Result.failure(Exception(retryException.message))
                        }
                    }
                } else {
                    tokenStorage.clearTokens()
                    Result.failure(UnauthorizedException("Session expired. Please login again."))
                }
            } else {
                Result.failure(Exception(e.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun refreshToken(): Result<Unit> {
        val refreshTokenValue = tokenStorage.getRefreshTokenOrNull()
            ?: return Result.failure(Exception("No refresh token"))

        return try {
            val response = apiService.refreshToken(RefreshTokenRequest(refreshTokenValue))
            tokenStorage.saveTokens(response.toAuthTokens())
            Result.success(Unit)
        } catch (e: ApiException) {
            tokenStorage.clearTokens()
            Result.failure(Exception(e.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

