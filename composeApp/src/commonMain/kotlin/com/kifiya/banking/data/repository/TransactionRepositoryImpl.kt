package com.kifiya.banking.data.repository

import com.kifiya.banking.data.local.TokenStorage
import com.kifiya.banking.data.mapper.toTransactions
import com.kifiya.banking.data.remote.api.BankingApiService
import com.kifiya.banking.domain.model.Transaction
import com.kifiya.banking.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val apiService: BankingApiService,
    private val tokenStorage: TokenStorage
) : TransactionRepository {

    private val apiHelper = AuthenticatedApiHelper(apiService, tokenStorage)

    override suspend fun getTransactions(
        accountId: Long,
        page: Int,
        size: Int
    ): Result<List<Transaction>> {
        return apiHelper.executeWithTokenRefresh { token ->
            apiService.getTransactions(accountId, page, size, token)
        }.map { response ->
            response.content.toTransactions()
        }.onFailure { error ->
            if (error !is UnauthorizedException) {
                return Result.failure(Exception("Failed to load transactions. Please try again."))
            }
        }
    }
}
