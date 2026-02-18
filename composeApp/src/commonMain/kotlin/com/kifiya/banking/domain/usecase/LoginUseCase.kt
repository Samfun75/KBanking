package com.kifiya.banking.domain.usecase

import com.kifiya.banking.domain.model.AuthTokens
import com.kifiya.banking.domain.repository.AuthRepository
import com.kifiya.banking.domain.util.ValidationException
import com.kifiya.banking.domain.util.Validators

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<AuthTokens> {
        if (!Validators.isValidUsername(username)) {
            return Result.failure(ValidationException("Username must be at least 3 characters"))
        }
        if (password.length < 6) {
            return Result.failure(ValidationException("Password must be at least 6 characters"))
        }

        return authRepository.login(username, password).onSuccess { tokens ->
            authRepository.saveTokens(tokens)
        }
    }
}

