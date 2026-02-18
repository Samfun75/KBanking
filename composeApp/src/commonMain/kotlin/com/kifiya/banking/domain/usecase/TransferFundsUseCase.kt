package com.kifiya.banking.domain.usecase

import com.kifiya.banking.domain.repository.AccountRepository
import com.kifiya.banking.domain.util.ValidationException
import com.kifiya.banking.domain.util.Validators

class TransferFundsUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(
        fromAccountNumber: String,
        toAccountNumber: String,
        amount: Double
    ): Result<Unit> {
        if (fromAccountNumber.isBlank()) {
            return Result.failure(ValidationException("Source account is required"))
        }
        if (toAccountNumber.isBlank()) {
            return Result.failure(ValidationException("Destination account is required"))
        }
        if (!Validators.isValidAmount(amount)) {
            return Result.failure(ValidationException("Amount must be positive"))
        }
        if (fromAccountNumber == toAccountNumber) {
            return Result.failure(ValidationException("Cannot transfer to the same account"))
        }

        return accountRepository.transfer(fromAccountNumber, toAccountNumber, amount).onSuccess {
            accountRepository.invalidateCache()
        }
    }
}

