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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kifiya.banking.presentation.components.InputField
import com.kifiya.banking.presentation.components.PrimaryButton
import com.kifiya.banking.presentation.components.SmallAccountCard
import com.kifiya.banking.presentation.components.SuccessDialog
import com.kifiya.banking.presentation.components.formatCurrency
import com.kifiya.banking.presentation.theme.PrimaryBlue
import com.kifiya.banking.presentation.theme.PrimaryBlueLight
import com.kifiya.banking.presentation.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    accountViewModel: AccountViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val accountsState by accountViewModel.accountsState.collectAsState()
    val transferState by accountViewModel.transferState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (accountsState.accounts.isEmpty()) {
            accountViewModel.loadAccounts()
        }
    }

    LaunchedEffect(Unit) {
        accountViewModel.sessionExpiredEvent.collect {
            onLogout()
        }
    }

    LaunchedEffect(transferState.error) {
        transferState.error?.let {
            snackbarHostState.showSnackbar(it)
            accountViewModel.clearError()
        }
    }

    if (transferState.isSuccess) {
        SuccessDialog(
            title = "Transfer Successful",
            message = "Your transfer has been completed successfully.",
            onDismiss = {
                accountViewModel.clearTransferSuccess()
                onNavigateBack()
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transfer Funds",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        accountViewModel.resetTransferState()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // From Account Section
                TransferSectionCard(
                    title = "Select Source Account",
                    icon = Icons.Default.AccountBalance
                ) {
                    if (accountsState.accounts.isEmpty()) {
                        Text(
                            text = "No accounts available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            accountsState.accounts.forEach { account ->
                                SmallAccountCard(
                                    account = account,
                                    onClick = { accountViewModel.selectFromAccount(account) },
                                    isSelected = transferState.fromAccount?.id == account.id
                                )
                            }
                        }
                    }
                }

                // Recipient Details Section
                TransferSectionCard(
                    title = "Recipient Details",
                    icon = Icons.Default.Person
                ) {
                    InputField(
                        value = transferState.toAccountNumber,
                        onValueChange = { accountViewModel.updateToAccountNumber(it) },
                        label = "Account Number",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Amount Section
                TransferSectionCard(
                    title = "Transfer Amount",
                    icon = Icons.Default.AttachMoney
                ) {
                    InputField(
                        value = transferState.amount,
                        onValueChange = { accountViewModel.updateAmount(it) },
                        label = "Amount (ETB)",
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                        onImeAction = { accountViewModel.transfer() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    transferState.fromAccount?.let { account ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Available Balance:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatCurrency(account.balance),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }


                // Transfer Button
                PrimaryButton(
                    text = "Transfer Now",
                    onClick = { accountViewModel.transfer() },
                    isLoading = transferState.isLoading,
                    enabled = transferState.fromAccount != null &&
                            transferState.toAccountNumber.isNotBlank() &&
                            transferState.amount.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TransferSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

