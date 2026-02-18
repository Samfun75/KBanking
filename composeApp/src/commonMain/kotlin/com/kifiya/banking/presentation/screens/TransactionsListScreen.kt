package com.kifiya.banking.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kifiya.banking.domain.model.Account
import com.kifiya.banking.domain.model.TransactionDirection
import com.kifiya.banking.presentation.components.LoadingIndicator
import com.kifiya.banking.presentation.components.TransactionItem
import com.kifiya.banking.presentation.theme.PrimaryBlue
import com.kifiya.banking.presentation.theme.PrimaryBlueLight
import com.kifiya.banking.presentation.viewmodel.AccountViewModel
import com.kifiya.banking.presentation.viewmodel.TransactionViewModel

enum class TransactionFilter(val label: String) {
    ALL("All"),
    INCOMING("Incoming"),
    OUTGOING("Outgoing")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(
    accountViewModel: AccountViewModel,
    transactionViewModel: TransactionViewModel,
    onLogout: () -> Unit
) {
    val accountsState by accountViewModel.accountsState.collectAsState()
    val transactionsState by transactionViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var transactionFilter by remember { mutableStateOf(TransactionFilter.ALL) }
    var accountDropdownExpanded by remember { mutableStateOf(false) }

    // Load accounts if not loaded
    LaunchedEffect(Unit) {
        if (accountsState.accounts.isEmpty()) {
            accountViewModel.loadAccounts()
        }
    }

    // Auto-select first account when accounts are loaded
    LaunchedEffect(accountsState.accounts) {
        if (accountsState.accounts.isNotEmpty() && selectedAccount == null) {
            selectedAccount = accountsState.accounts.first()
        }
    }

    // Load transactions when account is selected
    LaunchedEffect(selectedAccount) {
        selectedAccount?.let { account ->
            transactionViewModel.setAccount(account)
        }
    }

    LaunchedEffect(Unit) {
        transactionViewModel.sessionExpiredEvent.collect {
            onLogout()
        }
    }

    LaunchedEffect(transactionsState.error) {
        transactionsState.error?.let {
            snackbarHostState.showSnackbar(it)
            transactionViewModel.clearError()
        }
    }

    // Infinite scroll logic
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 2 && transactionsState.hasMore && !transactionsState.isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            transactionViewModel.loadMoreTransactions()
        }
    }

    // Filter transactions based on selected filter
    val filteredTransactions = remember(transactionsState.transactions, transactionFilter) {
        when (transactionFilter) {
            TransactionFilter.ALL -> transactionsState.transactions
            TransactionFilter.INCOMING -> transactionsState.transactions.filter {
                it.direction == TransactionDirection.CREDIT
            }
            TransactionFilter.OUTGOING -> transactionsState.transactions.filter {
                it.direction == TransactionDirection.DEBIT
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        selectedAccount?.let {
                            transactionViewModel.loadTransactions(it.id, forceRefresh = true)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlueLight)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                // Fixed Filter Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Filter Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Account Dropdown
                        ExposedDropdownMenuBox(
                            expanded = accountDropdownExpanded,
                            onExpandedChange = { accountDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedAccount?.let {
                                    "${it.accountType.name} - ****${it.accountNumber.takeLast(4)}"
                                } ?: "Select Account",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Account") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = accountDropdownExpanded,
                                onDismissRequest = { accountDropdownExpanded = false }
                            ) {
                                accountsState.accounts.forEach { account ->
                                    DropdownMenuItem(
                                        text = {
                                            Text("${account.accountType.name} - ****${account.accountNumber.takeLast(4)}")
                                        },
                                        onClick = {
                                            selectedAccount = account
                                            accountDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transaction Direction Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TransactionFilter.entries.forEach { filter ->
                                FilterChip(
                                    selected = transactionFilter == filter,
                                    onClick = { transactionFilter = filter },
                                    label = { Text(filter.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fixed Section Header
                Text(
                    text = "Transaction History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Scrollable Transaction List Only
                if (transactionsState.isLoading && transactionsState.transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading transactions...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (transactionsState.isLoading) {
                    // Show loading overlay when switching accounts but keeping existing data visible
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredTransactions, key = { "transaction_${it.id}" }) { transaction ->
                                TransactionItem(transaction = transaction)
                            }
                        }
                        // Loading overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (filteredTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (transactionFilter) {
                                TransactionFilter.INCOMING -> "No incoming transactions found"
                                TransactionFilter.OUTGOING -> "No outgoing transactions found"
                                TransactionFilter.ALL -> "No transactions found"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(2f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredTransactions, key = { "transaction_${it.id}" }) { transaction ->
                            TransactionItem(transaction = transaction)
                        }

                        if (transactionsState.isLoadingMore) {
                            item(key = "loading_more") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom spacing for navigation bar
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
