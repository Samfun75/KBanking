package com.kifiya.banking.domain.usecase

import com.kifiya.banking.domain.model.Transaction
import com.kifiya.banking.domain.repository.TransactionRepository

class GetTransactionsUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(accountId: Long, page: Int = 0, size: Int = 20): Result<List<Transaction>> {
        return transactionRepository.getTransactions(accountId, page, size)
    }
}

