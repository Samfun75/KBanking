package com.kifiya.banking

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kifiya.banking.presentation.navigation.NavGraph
import com.kifiya.banking.presentation.navigation.Screen
import com.kifiya.banking.presentation.theme.BankingTheme
import com.kifiya.banking.presentation.viewmodel.AuthViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    KoinContext {
        BankingTheme {
            val authViewModel: AuthViewModel = koinViewModel()
            val startDestination: Any = if (authViewModel.uiState.value.isLoggedIn) {
                Screen.Main
            } else {
                Screen.Login
            }
            NavGraph(
                startDestination = startDestination,
                authViewModel = authViewModel
            )
        }
    }
}

