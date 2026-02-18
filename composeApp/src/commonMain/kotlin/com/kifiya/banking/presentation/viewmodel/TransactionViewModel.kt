package com.kifiya.banking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kifiya.banking.data.repository.UnauthorizedException
import com.kifiya.banking.domain.model.Account
import com.kifiya.banking.domain.usecase.GetTransactionsUseCase
import com.kifiya.banking.presentation.viewmodel.state.TransactionsUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>()
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    private val pageSize = 20
    private var currentAccountId: Long? = null

    fun setAccount(account: Account) {
        _uiState.update { it.copy(account = account) }
        loadTransactions(account.id)
    }

    fun loadTransactions(accountId: Long, forceRefresh: Boolean = false) {
        currentAccountId = accountId

        viewModelScope.launch {
            if (forceRefresh) {
                _uiState.update { it.copy(transactions = emptyList(), currentPage = 0, hasMore = true) }
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            getTransactionsUseCase(accountId = accountId, page = 0, size = pageSize)
                .onSuccess { transactions ->
                    _uiState.update {
                        it.copy(
                            transactions = transactions,
                            isLoading = false,
                            currentPage = 0,
                            hasMore = transactions.size >= pageSize
                        )
                    }
                }
                .onFailure { error ->
                    handleError(error)
                }
        }
    }

    fun loadMoreTransactions() {
        val currentState = _uiState.value
        val accountId = currentAccountId ?: return

        if (currentState.isLoadingMore || !currentState.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = currentState.currentPage + 1

            getTransactionsUseCase(accountId = accountId, page = nextPage, size = pageSize)
                .onSuccess { newTransactions ->
                    _uiState.update {
                        it.copy(
                            transactions = it.transactions + newTransactions,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            hasMore = newTransactions.size >= pageSize
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoadingMore = false, error = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private suspend fun handleError(error: Throwable) {
        if (error is UnauthorizedException) {
            _sessionExpiredEvent.emit(Unit)
        }
        _uiState.update { it.copy(isLoading = false, error = error.message) }
    }
}

