package com.kifiya.banking.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kifiya.banking.domain.model.Account
import com.kifiya.banking.presentation.components.SmallAccountCard
import com.kifiya.banking.presentation.components.LoadingIndicator
import com.kifiya.banking.presentation.components.TransactionItem
import com.kifiya.banking.presentation.components.formatCurrency
import com.kifiya.banking.presentation.theme.PrimaryBlue
import com.kifiya.banking.presentation.theme.PrimaryBlueLight
import com.kifiya.banking.presentation.viewmodel.AccountViewModel
import com.kifiya.banking.presentation.viewmodel.AuthViewModel
import com.kifiya.banking.presentation.viewmodel.TransactionViewModel
import org.jetbrains.compose.resources.painterResource
import kifiyabanking.composeapp.generated.resources.Res
import kifiyabanking.composeapp.generated.resources.user

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    accountViewModel: AccountViewModel,
    transactionViewModel: TransactionViewModel,
    onNavigateToTransfer: () -> Unit,
    onNavigateToBills: () -> Unit,
    onNavigateToRecharge: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAccountTransactions: (Account) -> Unit,
    onLogout: () -> Unit
) {
    val accountsState by accountViewModel.accountsState.collectAsState()
    val transactionsState by transactionViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        accountViewModel.loadAccounts()
    }

    // Load transactions for the first account
    LaunchedEffect(accountsState.accounts) {
        if (accountsState.accounts.isNotEmpty() && transactionsState.transactions.isEmpty()) {
            val firstAccount = accountsState.accounts.first()
            transactionViewModel.setAccount(firstAccount)
        }
    }

    LaunchedEffect(Unit) {
        accountViewModel.sessionExpiredEvent.collect {
            onLogout()
        }
    }

    LaunchedEffect(accountsState.error) {
        accountsState.error?.let {
            snackbarHostState.showSnackbar(it)
            accountViewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(Res.drawable.user),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Welcome back",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "John Doe",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { accountViewModel.loadAccounts(forceRefresh = true) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlueLight)
                        )
                    )
            )

            if (accountsState.isLoading && accountsState.accounts.isEmpty()) {
                LoadingIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Balance Card
                    TotalBalanceCard(totalBalance = accountsState.totalBalance)

                    // Quick Actions Section
                    QuickActionsSection(
                        onTransferClick = onNavigateToTransfer,
                        onBillsClick = onNavigateToBills,
                        onRechargeClick = onNavigateToRecharge,
                        onMoreClick = onNavigateToMore
                    )

                    // My Accounts Section
                    SectionHeader(
                        title = "My Accounts",
                        onViewAllClick = onNavigateToAccounts
                    )

                    // Show first 2 accounts
                    accountsState.accounts.take(2).forEach { account ->
                        SmallAccountCard(
                            account = account,
                            onClick = { onNavigateToAccountTransactions(account) }
                        )
                    }

                    // Recent Transactions Section
                    SectionHeader(
                        title = "Recent Transactions",
                        onViewAllClick = onNavigateToTransactions
                    )

                    // Show first 2 transactions
                    if (transactionsState.isLoading) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else if (transactionsState.transactions.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No recent transactions",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        transactionsState.transactions.take(2).forEach { transaction ->
                            TransactionItem(transaction = transaction)
                        }
                    }

                    // Bottom spacing for navigation bar
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(totalBalance: Double) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(totalBalance),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
}

@Composable
private fun QuickActionsSection(
    onTransferClick: () -> Unit,
    onBillsClick: () -> Unit,
    onRechargeClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    label = "Transfer",
                    onClick = onTransferClick
                )
                QuickActionButton(
                    icon = Icons.Default.Receipt,
                    label = "Bills",
                    onClick = onBillsClick
                )
                QuickActionButton(
                    icon = Icons.Default.PhoneAndroid,
                    label = "Recharge",
                    onClick = onRechargeClick
                )
                QuickActionButton(
                    icon = Icons.Default.MoreHoriz,
                    label = "More",
                    onClick = onMoreClick
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAllClick) {
            Text(
                text = "View All",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
