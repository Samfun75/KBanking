package com.kifiya.banking.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kifiya.banking.presentation.screens.BillsScreen
import com.kifiya.banking.presentation.screens.LoginScreen
import com.kifiya.banking.presentation.screens.MainScreen
import com.kifiya.banking.presentation.screens.MoreServicesScreen
import com.kifiya.banking.presentation.screens.RechargeScreen
import com.kifiya.banking.presentation.screens.RegisterScreen
import com.kifiya.banking.presentation.screens.TransactionHistoryScreen
import com.kifiya.banking.presentation.screens.TransferScreen
import com.kifiya.banking.presentation.viewmodel.AccountViewModel
import com.kifiya.banking.presentation.viewmodel.AuthViewModel
import com.kifiya.banking.presentation.viewmodel.TransactionViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Any = Screen.Login,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val accountViewModel: AccountViewModel = koinViewModel()
    val transactionViewModel: TransactionViewModel = koinViewModel()

    var selectedAccountId by rememberSaveable { mutableStateOf<Long?>(null) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Login> {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Register> {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Main> {
            MainScreen(
                authViewModel = authViewModel,
                accountViewModel = accountViewModel,
                transactionViewModel = transactionViewModel,
                onNavigateToTransfer = {
                    navController.navigate(Screen.Transfer)
                },
                onNavigateToBills = {
                    navController.navigate(Screen.Bills)
                },
                onNavigateToRecharge = {
                    navController.navigate(Screen.Recharge)
                },
                onNavigateToMore = {
                    navController.navigate(Screen.MoreServices)
                },
                onNavigateToAccountTransactions = { account, sourceRoute ->
                    selectedAccountId = account.id
                    navController.navigate(Screen.TransactionHistory(account.id, sourceRoute))
                },
                onNavigateToAllTransactions = {
                    navController.navigate(Screen.TransactionHistory(null, null))
                },
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Transfer> {
            TransferScreen(
                accountViewModel = accountViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Bills> {
            BillsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Recharge> {
            RechargeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.MoreServices> {
            MoreServicesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.TransactionHistory> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.TransactionHistory>()
            val accountId = route.accountId ?: selectedAccountId
            val account = accountId?.let { id ->
                accountViewModel.accountsState.value.accounts.find { it.id == id }
            } ?: accountViewModel.accountsState.value.accounts.firstOrNull()

            account?.let {
                TransactionHistoryScreen(
                    account = it,
                    transactionViewModel = transactionViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

