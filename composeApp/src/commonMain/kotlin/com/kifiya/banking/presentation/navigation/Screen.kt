package com.kifiya.banking.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object Main : Screen()

    @Serializable
    data object Transfer : Screen()

    @Serializable
    data object Bills : Screen()

    @Serializable
    data object Recharge : Screen()

    @Serializable
    data object MoreServices : Screen()

    @Serializable
    data object Accounts : Screen()

    @Serializable
    data class TransactionHistory(val accountId: Long? = null, val sourceRoute: String? = null) : Screen()

    @Serializable
    data object Profile : Screen()
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    data object Accounts : BottomNavItem("accounts", "Accounts", Icons.Default.AccountBalanceWallet)
    data object Transactions : BottomNavItem("transactions", "Transactions", Icons.Default.Receipt)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

