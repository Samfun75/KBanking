package com.kifiya.banking.presentation.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kifiya.banking.presentation.components.InputField
import com.kifiya.banking.presentation.components.PrimaryButton
import com.kifiya.banking.presentation.theme.PrimaryBlue
import com.kifiya.banking.presentation.theme.PrimaryBlueLight
import com.kifiya.banking.presentation.viewmodel.AuthNavigationEvent
import com.kifiya.banking.presentation.viewmodel.AuthViewModel
import com.kifiya.banking.presentation.viewmodel.RegisterField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val registerForm by viewModel.registerForm.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AuthNavigationEvent.NavigateToLogin -> onNavigateToLogin()
                else -> {}
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Account",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlueLight)
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Join Kifiya Banking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fill in your details to get started",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Scrollable Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Name Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InputField(
                            value = registerForm.firstName,
                            onValueChange = { viewModel.updateRegisterField(RegisterField.FIRST_NAME, it) },
                            label = "First Name",
                            error = registerForm.firstNameError,
                            modifier = Modifier.weight(1f)
                        )
                        InputField(
                            value = registerForm.lastName,
                            onValueChange = { viewModel.updateRegisterField(RegisterField.LAST_NAME, it) },
                            label = "Last Name",
                            error = registerForm.lastNameError,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    InputField(
                        value = registerForm.username,
                        onValueChange = { viewModel.updateRegisterField(RegisterField.USERNAME, it) },
                        label = "Username",
                        error = registerForm.usernameError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    InputField(
                        value = registerForm.email,
                        onValueChange = { viewModel.updateRegisterField(RegisterField.EMAIL, it) },
                        label = "Email Address",
                        error = registerForm.emailError,
                        keyboardType = KeyboardType.Email,
                        modifier = Modifier.fillMaxWidth()
                    )

                    InputField(
                        value = registerForm.phoneNumber,
                        onValueChange = { viewModel.updateRegisterField(RegisterField.PHONE_NUMBER, it) },
                        label = "Phone Number",
                        error = registerForm.phoneNumberError,
                        keyboardType = KeyboardType.Phone,
                        modifier = Modifier.fillMaxWidth()
                    )

                    InputField(
                        value = registerForm.password,
                        onValueChange = { viewModel.updateRegisterField(RegisterField.PASSWORD, it) },
                        label = "Password",
                        error = registerForm.passwordError,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                        modifier = Modifier.fillMaxWidth()
                    )

                    InputField(
                        value = registerForm.confirmPassword,
                        onValueChange = { viewModel.updateRegisterField(RegisterField.CONFIRM_PASSWORD, it) },
                        label = "Confirm Password",
                        error = registerForm.confirmPasswordError,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        onImeAction = { viewModel.register() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Fixed Bottom Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryButton(
                    text = "Create Account",
                    onClick = { viewModel.register() },
                    isLoading = uiState.isLoading,
                    enabled = registerForm.username.isNotBlank() &&
                            registerForm.password.isNotBlank() &&
                            registerForm.confirmPassword.isNotBlank() &&
                            registerForm.firstName.isNotBlank() &&
                            registerForm.lastName.isNotBlank() &&
                            registerForm.email.isNotBlank() &&
                            registerForm.phoneNumber.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateBack() }
                    )
                }
            }
        }
    }
}

