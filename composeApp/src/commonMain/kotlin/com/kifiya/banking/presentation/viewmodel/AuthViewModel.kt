package com.kifiya.banking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kifiya.banking.domain.repository.AuthRepository
import com.kifiya.banking.domain.usecase.LoginUseCase
import com.kifiya.banking.domain.usecase.RegisterUseCase
import com.kifiya.banking.domain.util.ValidationException
import com.kifiya.banking.presentation.viewmodel.state.AuthUiState
import com.kifiya.banking.presentation.viewmodel.state.LoginFormState
import com.kifiya.banking.presentation.viewmodel.state.RegisterFormState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()

    private val _registerForm = MutableStateFlow(RegisterFormState())
    val registerForm: StateFlow<RegisterFormState> = _registerForm.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        _uiState.update { it.copy(isLoggedIn = authRepository.isLoggedIn()) }
    }

    fun updateLoginUsername(username: String) {
        _loginForm.update { it.copy(username = username, usernameError = null) }
    }

    fun updateLoginPassword(password: String) {
        _loginForm.update { it.copy(password = password, passwordError = null) }
    }

    fun login() {
        viewModelScope.launch {
            val form = _loginForm.value
            _uiState.update { it.copy(isLoading = true, error = null) }

            loginUseCase(form.username, form.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToDashboard)
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ValidationException -> error.message ?: "Validation error"
                        else -> error.message ?: "Login failed"
                    }
                    _uiState.update { it.copy(isLoading = false, error = message) }
                }
        }
    }

    fun updateRegisterField(field: RegisterField, value: String) {
        _registerForm.update { form ->
            when (field) {
                RegisterField.USERNAME -> form.copy(username = value, usernameError = null)
                RegisterField.PASSWORD -> form.copy(password = value, passwordError = null)
                RegisterField.CONFIRM_PASSWORD -> form.copy(confirmPassword = value, confirmPasswordError = null)
                RegisterField.FIRST_NAME -> form.copy(firstName = value, firstNameError = null)
                RegisterField.LAST_NAME -> form.copy(lastName = value, lastNameError = null)
                RegisterField.EMAIL -> form.copy(email = value, emailError = null)
                RegisterField.PHONE_NUMBER -> form.copy(phoneNumber = value, phoneNumberError = null)
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            val form = _registerForm.value

            if (form.password != form.confirmPassword) {
                _registerForm.update { it.copy(confirmPasswordError = "Passwords do not match") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            registerUseCase(
                username = form.username,
                password = form.password,
                firstName = form.firstName,
                lastName = form.lastName,
                email = form.email,
                phoneNumber = form.phoneNumber
            )
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToLogin)
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ValidationException -> error.message ?: "Validation error"
                        else -> error.message ?: "Registration failed"
                    }
                    _uiState.update { it.copy(isLoading = false, error = message) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { AuthUiState() }
            _loginForm.update { LoginFormState() }
            _navigationEvent.emit(AuthNavigationEvent.NavigateToLogin)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

enum class RegisterField {
    USERNAME, PASSWORD, CONFIRM_PASSWORD, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER
}

sealed class AuthNavigationEvent {
    data object NavigateToDashboard : AuthNavigationEvent()
    data object NavigateToLogin : AuthNavigationEvent()
}

