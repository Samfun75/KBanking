package com.kifiya.banking.domain.repository

import com.kifiya.banking.domain.model.Transaction

interface TransactionRepository {
    suspend fun getTransactions(
        accountId: Long,
        page: Int,
        size: Int
    ): Result<List<Transaction>>
}

