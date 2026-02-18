package com.kifiya.banking.domain.util

object Validators {
    fun isValidUsername(username: String): Boolean {
        return username.length >= 3
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUppercase && hasLowercase && hasDigit
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(Regex(emailRegex))
    }

    fun isValidAmount(amount: Double): Boolean {
        return amount > 0
    }

    fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length >= 10 && phone.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }
}

class ValidationException(message: String) : Exception(message)

