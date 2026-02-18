package com.kifiya.banking.data.mapper

import com.kifiya.banking.data.remote.dto.*
import com.kifiya.banking.domain.model.*
import kotlinx.datetime.Instant

fun LoginResponse.toAuthTokens(): AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresIn = expiresIn
    )
}

fun UserDto.toUser(): User {
    return User(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber
    )
}

fun AccountDto.toAccount(): Account {
    return Account(
        id = id,
        accountNumber = accountNumber,
        accountType = when (accountType.uppercase()) {
            "SAVINGS" -> AccountType.SAVINGS
            "CHECKING" -> AccountType.CHECKING
            "BUSINESS" -> AccountType.BUSINESS
            else -> AccountType.SAVINGS
        },
        balance = balance,
        userId = userId
    )
}

fun TransactionDto.toTransaction(): Transaction {
    return Transaction(
        id = id,
        accountId = accountId,
        amount = amount,
        type = when (type.uppercase()) {
            "TRANSFER" -> TransactionType.TRANSFER
            "DEPOSIT" -> TransactionType.DEPOSIT
            "WITHDRAWAL" -> TransactionType.WITHDRAWAL
            "PAYMENT" -> TransactionType.PAYMENT
            else -> TransactionType.TRANSFER
        },
        direction = when (direction.uppercase()) {
            "DEBIT" -> TransactionDirection.DEBIT
            "CREDIT" -> TransactionDirection.CREDIT
            else -> TransactionDirection.DEBIT
        },
        timestamp = try {
            Instant.parse(timestamp)
        } catch (e: Exception) {
            Instant.DISTANT_PAST
        },
        description = description,
        relatedAccountNumber = relatedAccountNumber
    )
}

fun List<AccountDto>.toAccounts(): List<Account> = map { it.toAccount() }
fun List<TransactionDto>.toTransactions(): List<Transaction> = map { it.toTransaction() }

