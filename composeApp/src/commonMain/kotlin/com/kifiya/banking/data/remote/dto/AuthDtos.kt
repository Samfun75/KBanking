package com.kifiya.banking.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class RegisterRequest(
    val username: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

