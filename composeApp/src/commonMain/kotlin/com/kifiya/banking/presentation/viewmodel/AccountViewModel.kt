package com.kifiya.banking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kifiya.banking.data.repository.UnauthorizedException
import com.kifiya.banking.domain.model.Account
import com.kifiya.banking.domain.usecase.GetAccountsUseCase
import com.kifiya.banking.domain.usecase.TransferFundsUseCase
import com.kifiya.banking.presentation.viewmodel.state.AccountsUiState
import com.kifiya.banking.presentation.viewmodel.state.TransferUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val transferFundsUseCase: TransferFundsUseCase
) : ViewModel() {

    private val _accountsState = MutableStateFlow(AccountsUiState())
    val accountsState: StateFlow<AccountsUiState> = _accountsState.asStateFlow()

    private val _transferState = MutableStateFlow(TransferUiState())
    val transferState: StateFlow<TransferUiState> = _transferState.asStateFlow()

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>()
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    private val pageSize = 10

    fun loadAccounts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                _accountsState.update { it.copy(accounts = emptyList(), currentPage = 0, hasMore = true) }
            }

            _accountsState.update { it.copy(isLoading = true, error = null) }

            getAccountsUseCase(page = 0, size = pageSize, forceRefresh = forceRefresh)
                .onSuccess { accounts ->
                    _accountsState.update {
                        it.copy(
                            accounts = accounts,
                            isLoading = false,
                            currentPage = 0,
                            hasMore = accounts.size >= pageSize,
                            totalBalance = accounts.sumOf { acc -> acc.balance }
                        )
                    }
                }
                .onFailure { error ->
                    handleError(error)
                }
        }
    }

    fun loadMoreAccounts() {
        val currentState = _accountsState.value
        if (currentState.isLoadingMore || !currentState.hasMore) return

        viewModelScope.launch {
            _accountsState.update { it.copy(isLoadingMore = true) }
            val nextPage = currentState.currentPage + 1

            getAccountsUseCase(page = nextPage, size = pageSize)
                .onSuccess { newAccounts ->
                    _accountsState.update {
                        val allAccounts = it.accounts + newAccounts
                        it.copy(
                            accounts = allAccounts,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            hasMore = newAccounts.size >= pageSize,
                            totalBalance = allAccounts.sumOf { acc -> acc.balance }
                        )
                    }
                }
                .onFailure { error ->
                    _accountsState.update { it.copy(isLoadingMore = false, error = error.message) }
                }
        }
    }

    fun selectFromAccount(account: Account) {
        _transferState.update { it.copy(fromAccount = account, error = null) }
    }

    fun updateToAccountNumber(accountNumber: String) {
        _transferState.update { it.copy(toAccountNumber = accountNumber, error = null) }
    }

    fun updateAmount(amount: String) {
        _transferState.update { it.copy(amount = amount, error = null) }
    }

    fun transfer() {
        viewModelScope.launch {
            val state = _transferState.value
            val fromAccount = state.fromAccount

            if (fromAccount == null) {
                _transferState.update { it.copy(error = "Please select source account") }
                return@launch
            }

            val amount = state.amount.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                _transferState.update { it.copy(error = "Please enter a valid amount") }
                return@launch
            }

            if (amount > fromAccount.balance) {
                _transferState.update { it.copy(error = "Insufficient funds") }
                return@launch
            }

            _transferState.update { it.copy(isLoading = true, error = null) }

            transferFundsUseCase(
                fromAccountNumber = fromAccount.accountNumber,
                toAccountNumber = state.toAccountNumber,
                amount = amount
            )
                .onSuccess {
                    _transferState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            toAccountNumber = "",
                            amount = ""
                        )
                    }
                    loadAccounts(forceRefresh = true)
                }
                .onFailure { error ->
                    if (error is UnauthorizedException) {
                        _sessionExpiredEvent.emit(Unit)
                    }
                    _transferState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun resetTransferState() {
        _transferState.update { TransferUiState() }
    }

    fun clearTransferSuccess() {
        _transferState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _accountsState.update { it.copy(error = null) }
        _transferState.update { it.copy(error = null) }
    }

    private suspend fun handleError(error: Throwable) {
        if (error is UnauthorizedException) {
            _sessionExpiredEvent.emit(Unit)
        }
        _accountsState.update { it.copy(isLoading = false, error = error.message) }
    }
}

