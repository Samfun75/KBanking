package com.kifiya.banking.domain.usecase

import com.kifiya.banking.domain.model.User
import com.kifiya.banking.domain.repository.AuthRepository
import com.kifiya.banking.domain.util.ValidationException
import com.kifiya.banking.domain.util.Validators

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ): Result<User> {
        if (!Validators.isValidUsername(username)) {
            return Result.failure(ValidationException("Username must be at least 3 characters"))
        }
        if (!Validators.isValidPassword(password)) {
            return Result.failure(ValidationException("Password must be at least 8 characters with mixed case and a number"))
        }
        if (!Validators.isValidEmail(email)) {
            return Result.failure(ValidationException("Invalid email format"))
        }
        if (firstName.isBlank()) {
            return Result.failure(ValidationException("First name is required"))
        }
        if (lastName.isBlank()) {
            return Result.failure(ValidationException("Last name is required"))
        }
        if (phoneNumber.isBlank()) {
            return Result.failure(ValidationException("Phone number is required"))
        }

        return authRepository.register(username, password, firstName, lastName, email, phoneNumber)
    }
}

