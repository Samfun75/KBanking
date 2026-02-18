package com.kifiya.banking.domain.model

import kotlinx.datetime.Instant

data class Transaction(
    val id: Long,
    val accountId: Long,
    val amount: Double,
    val type: TransactionType,
    val direction: TransactionDirection,
    val timestamp: Instant,
    val description: String,
    val relatedAccountNumber: String?
)

enum class TransactionType {
    TRANSFER,
    DEPOSIT,
    WITHDRAWAL,
    PAYMENT
}

enum class TransactionDirection {
    DEBIT,
    CREDIT
}

