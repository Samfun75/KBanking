package com.kifiya.banking.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: Long,
    val accountId: Long,
    val amount: Double,
    val type: String,
    val direction: String,
    val timestamp: String,
    val description: String,
    val relatedAccountNumber: String? = null
)

