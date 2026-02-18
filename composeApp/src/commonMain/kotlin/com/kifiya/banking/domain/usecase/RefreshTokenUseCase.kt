package com.kifiya.banking.domain.usecase

import com.kifiya.banking.domain.model.AuthTokens
import com.kifiya.banking.domain.repository.AuthRepository

class RefreshTokenUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<AuthTokens> {
        val currentTokens = authRepository.getStoredTokens()
            ?: return Result.failure(Exception("No stored tokens found"))

        return authRepository.refreshToken(currentTokens.refreshToken).onSuccess { newTokens ->
            authRepository.saveTokens(newTokens)
        }.onFailure {
            authRepository.clearTokens()
        }
    }
}

