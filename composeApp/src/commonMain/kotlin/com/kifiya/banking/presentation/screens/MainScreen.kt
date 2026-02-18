package com.kifiya.banking.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kifiya.banking.domain.model.Account
import com.kifiya.banking.presentation.components.BottomNavigationBar
import com.kifiya.banking.presentation.navigation.BottomNavItem
import com.kifiya.banking.presentation.viewmodel.AccountViewModel
import com.kifiya.banking.presentation.viewmodel.AuthViewModel
import com.kifiya.banking.presentation.viewmodel.TransactionViewModel

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    accountViewModel: AccountViewModel,
    transactionViewModel: TransactionViewModel,
    onNavigateToTransfer: () -> Unit,
    onNavigateToBills: () -> Unit,
    onNavigateToRecharge: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToAccountTransactions: (Account, String) -> Unit,
    onNavigateToAllTransactions: () -> Unit,
    onLogout: () -> Unit,
    initialRoute: String = BottomNavItem.Home.route
) {
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Accounts,
        BottomNavItem.Transactions,
        BottomNavItem.Profile
    )

    var currentRoute by rememberSaveable { mutableStateOf(initialRoute) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                currentRoute = currentRoute,
                onItemClick = { item ->
                    currentRoute = item.route
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            when (currentRoute) {
                BottomNavItem.Home.route -> {
                    HomeScreen(
                        authViewModel = authViewModel,
                        accountViewModel = accountViewModel,
                        transactionViewModel = transactionViewModel,
                        onNavigateToTransfer = onNavigateToTransfer,
                        onNavigateToBills = onNavigateToBills,
                        onNavigateToRecharge = onNavigateToRecharge,
                        onNavigateToMore = onNavigateToMore,
                        onNavigateToAccounts = { currentRoute = BottomNavItem.Accounts.route },
                        onNavigateToTransactions = { currentRoute = BottomNavItem.Transactions.route },
                        onNavigateToAccountTransactions = { account ->
                            onNavigateToAccountTransactions(account, currentRoute)
                        },
                        onLogout = onLogout
                    )
                }
                BottomNavItem.Accounts.route -> {
                    AccountsScreen(
                        accountViewModel = accountViewModel,
                        onNavigateToTransactions = { account ->
                            onNavigateToAccountTransactions(account, currentRoute)
                        },
                        onLogout = onLogout
                    )
                }
                BottomNavItem.Transactions.route -> {
                    TransactionsListScreen(
                        accountViewModel = accountViewModel,
                        transactionViewModel = transactionViewModel,
                        onLogout = onLogout
                    )
                }
                BottomNavItem.Profile.route -> {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

