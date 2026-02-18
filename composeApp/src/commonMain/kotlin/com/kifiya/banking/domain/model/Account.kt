package com.kifiya.banking.domain.model

data class Account(
    val id: Long,
    val accountNumber: String,
    val accountType: AccountType,
    val balance: Double,
    val userId: Long
)

enum class AccountType {
    SAVINGS,
    CHECKING,
    BUSINESS
}

