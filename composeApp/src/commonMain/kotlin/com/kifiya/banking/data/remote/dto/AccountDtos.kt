package com.kifiya.banking.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Long,
    val accountNumber: String,
    val accountType: String,
    val balance: Double,
    val userId: Long
)

@Serializable
data class CreateAccountRequest(
    val accountType: String
)

@Serializable
data class TransferRequest(
    val fromAccountNumber: String,
    val toAccountNumber: String,
    val amount: Double
)

@Serializable
data class PaginatedResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

