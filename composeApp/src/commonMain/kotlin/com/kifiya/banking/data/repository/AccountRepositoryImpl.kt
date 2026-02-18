package com.kifiya.banking.data.repository

import com.kifiya.banking.data.local.TokenStorage
import com.kifiya.banking.data.mapper.toAccount
import com.kifiya.banking.data.mapper.toAccounts
import com.kifiya.banking.data.remote.api.BankingApiService
import com.kifiya.banking.data.remote.dto.CreateAccountRequest
import com.kifiya.banking.data.remote.dto.TransferRequest
import com.kifiya.banking.domain.model.Account
import com.kifiya.banking.domain.repository.AccountRepository
import kotlin.time.Clock

class AccountRepositoryImpl(
    private val apiService: BankingApiService,
    private val tokenStorage: TokenStorage
) : AccountRepository {

    private val apiHelper = AuthenticatedApiHelper(apiService, tokenStorage)
    private var cachedAccounts: List<Account> = emptyList()
    private var cacheTimestamp: Long = 0
    private val cacheValidityMs = 60_000L // 1 minute cache validity

    override suspend fun getAccounts(page: Int, size: Int): Result<List<Account>> {
        return apiHelper.executeWithTokenRefresh { token ->
            apiService.getAccounts(page, size, token)
        }.map { response ->
            val accounts = response.content.toAccounts()
            if (page == 0) {
                cachedAccounts = accounts
                cacheTimestamp = Clock.System.now().toEpochMilliseconds()
            } else {
                cachedAccounts = cachedAccounts + accounts
            }
            accounts
        }.onFailure { error ->
            if (error !is UnauthorizedException) {
                return Result.failure(Exception("Failed to load accounts. Please try again."))
            }
        }
    }

    override suspend fun getAccountById(accountId: Long): Result<Account> {
        return apiHelper.executeWithTokenRefresh { token ->
            apiService.getAccountById(accountId, token)
        }.map { it.toAccount() }
        .onFailure { error ->
            if (error !is UnauthorizedException) {
                return Result.failure(Exception("Failed to load account details."))
            }
        }
    }

    override suspend fun createAccount(accountType: String): Result<Account> {
        return apiHelper.executeWithTokenRefresh { token ->
            apiService.createAccount(CreateAccountRequest(accountType), token)
        }.map {
            invalidateCache()
            it.toAccount()
        }.onFailure { error ->
            if (error !is UnauthorizedException) {
                return Result.failure(Exception("Failed to create account. Please try again."))
            }
        }
    }

    override suspend fun transfer(
        fromAccountNumber: String,
        toAccountNumber: String,
        amount: Double
    ): Result<Unit> {
        return apiHelper.executeWithTokenRefresh { token ->
            apiService.transfer(
                TransferRequest(fromAccountNumber, toAccountNumber, amount),
                token
            )
        }.onFailure { error ->
            if (error !is UnauthorizedException) {
                val message = error.message ?: "Transfer failed"
                return Result.failure(Exception(message))
            }
        }
    }

    override fun getCachedAccounts(): List<Account> {
        val now = Clock.System.now().toEpochMilliseconds()
        return if (now - cacheTimestamp < cacheValidityMs) {
            cachedAccounts
        } else {
            emptyList()
        }
    }

    override fun invalidateCache() {
        cachedAccounts = emptyList()
        cacheTimestamp = 0
    }
}

class UnauthorizedException(message: String) : Exception(message)
