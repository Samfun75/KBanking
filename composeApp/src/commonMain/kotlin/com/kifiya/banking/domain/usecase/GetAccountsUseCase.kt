package com.kifiya.banking.domain.usecase

import com.kifiya.banking.domain.model.Account
import com.kifiya.banking.domain.repository.AccountRepository

class GetAccountsUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(page: Int = 0, size: Int = 10, forceRefresh: Boolean = false): Result<List<Account>> {
        if (!forceRefresh && page == 0) {
            val cached = accountRepository.getCachedAccounts()
            if (cached.isNotEmpty()) {
                return Result.success(cached)
            }
        }
        return accountRepository.getAccounts(page, size)
    }
}

