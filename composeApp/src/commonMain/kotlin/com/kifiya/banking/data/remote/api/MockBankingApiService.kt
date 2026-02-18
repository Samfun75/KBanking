package com.kifiya.banking.data.remote.api

import com.kifiya.banking.data.remote.dto.*
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Clock

class MockBankingApiService : BankingApiService {

    // ============================================
    // MOCK DATA - This entire class returns fake data
    // to simulate network responses for development
    // Access tokens expire after 5 minutes (300 seconds)
    // ============================================

    companion object {
        private const val ACCESS_TOKEN_VALIDITY_MS = 5 * 60 * 1000L // 5 minutes in milliseconds
        private const val REFRESH_TOKEN_VALIDITY_MS = 7 * 24 * 60 * 60 * 1000L // 7 days
    }

    private val mockUsers = mutableMapOf(
        "testuser" to MockUser(
            id = 1L,
            username = "testuser",
            password = "Test1234",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "+1234567890"
        )
    )

    private var userIdCounter = 2L
    private var accountIdCounter = 3L
    private var transactionIdCounter = 100L

    private val mockAccounts = mutableListOf(
        AccountDto(1L, "1234567890", "SAVINGS", 15000.00, 1L),
        AccountDto(2L, "0987654321", "CHECKING", 8500.50, 1L)
    )

    private val mockTransactions = mutableListOf(
        TransactionDto(1L, 1L, 500.0, "TRANSFER", "DEBIT", "2026-02-15T10:30:00Z", "Transfer to savings", "0987654321"),
        TransactionDto(2L, 1L, 1200.0, "DEPOSIT", "CREDIT", "2026-02-14T14:20:00Z", "Salary deposit", null),
        TransactionDto(3L, 1L, 75.50, "PAYMENT", "DEBIT", "2026-02-13T09:15:00Z", "Electric bill payment", null),
        TransactionDto(4L, 1L, 200.0, "WITHDRAWAL", "DEBIT", "2026-02-12T16:45:00Z", "ATM withdrawal", null),
        TransactionDto(5L, 1L, 3000.0, "TRANSFER", "CREDIT", "2026-02-11T11:00:00Z", "Transfer from business", "5555666677"),
        TransactionDto(6L, 2L, 500.0, "TRANSFER", "CREDIT", "2026-02-15T10:30:00Z", "Transfer from checking", "1234567890"),
        TransactionDto(7L, 2L, 150.0, "PAYMENT", "DEBIT", "2026-02-14T08:00:00Z", "Internet subscription", null),
        TransactionDto(8L, 2L, 2500.0, "DEPOSIT", "CREDIT", "2026-02-10T12:30:00Z", "Freelance payment", null)
    )

    override suspend fun login(request: LoginRequest): LoginResponse {
        delay(800) // Simulate network delay
        val user = mockUsers[request.username]
        if (user == null || user.password != request.password) {
            throw ApiException(401, "Invalid username or password")
        }
        val now = Clock.System.now().toEpochMilliseconds()
        return LoginResponse(
            accessToken = "mock_access_token_${now}",
            refreshToken = "mock_refresh_token_${now}",
            expiresIn = 300 // 5 minutes in seconds
        )
    }

    override suspend fun register(request: RegisterRequest): UserDto {
        delay(1000) // Simulate network delay
        if (mockUsers.containsKey(request.username)) {
            throw ApiException(409, "Username already exists")
        }
        if (mockUsers.values.any { it.email == request.email }) {
            throw ApiException(409, "Email already registered")
        }

        val newUser = MockUser(
            id = userIdCounter++,
            username = request.username,
            password = request.passwordHash,
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phoneNumber = request.phoneNumber
        )
        mockUsers[request.username] = newUser

        // Create default account for new user
        mockAccounts.add(
            AccountDto(accountIdCounter++, generateAccountNumber(), "SAVINGS", 0.0, newUser.id)
        )

        return UserDto(
            id = newUser.id,
            username = newUser.username,
            firstName = newUser.firstName,
            lastName = newUser.lastName,
            email = newUser.email,
            phoneNumber = newUser.phoneNumber
        )
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): LoginResponse {
        delay(500) // Simulate network delay
        if (!request.refreshToken.startsWith("mock_refresh_token_")) {
            throw ApiException(401, "Invalid refresh token")
        }
        // Check if refresh token itself is expired (7 days)
        val refreshTokenTimestamp = request.refreshToken.removePrefix("mock_refresh_token_").toLongOrNull()
        if (refreshTokenTimestamp != null) {
            val now = Clock.System.now().toEpochMilliseconds()
            if (now - refreshTokenTimestamp > REFRESH_TOKEN_VALIDITY_MS) {
                throw ApiException(401, "Refresh token expired. Please login again.")
            }
        }
        val now = Clock.System.now().toEpochMilliseconds()
        return LoginResponse(
            accessToken = "mock_access_token_${now}",
            refreshToken = "mock_refresh_token_${now}",
            expiresIn = 300 // 5 minutes in seconds
        )
    }

    override suspend fun getAccounts(page: Int, size: Int, accessToken: String): PaginatedResponse<AccountDto> {
        delay(600) // Simulate network delay
        validateToken(accessToken)

        val startIndex = page * size
        val endIndex = minOf(startIndex + size, mockAccounts.size)
        val pageContent = if (startIndex < mockAccounts.size) {
            mockAccounts.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PaginatedResponse(
            content = pageContent,
            totalPages = (mockAccounts.size + size - 1) / size,
            totalElements = mockAccounts.size.toLong(),
            number = page,
            size = size,
            first = page == 0,
            last = endIndex >= mockAccounts.size
        )
    }

    override suspend fun getAccountById(accountId: Long, accessToken: String): AccountDto {
        delay(400) // Simulate network delay
        validateToken(accessToken)
        return mockAccounts.find { it.id == accountId }
            ?: throw ApiException(404, "Account not found")
    }

    override suspend fun createAccount(request: CreateAccountRequest, accessToken: String): AccountDto {
        delay(800) // Simulate network delay
        validateToken(accessToken)

        val newAccount = AccountDto(
            id = accountIdCounter++,
            accountNumber = generateAccountNumber(),
            accountType = request.accountType,
            balance = 0.0,
            userId = 1L
        )
        mockAccounts.add(newAccount)
        return newAccount
    }

    override suspend fun transfer(request: TransferRequest, accessToken: String) {
        delay(1000) // Simulate network delay
        validateToken(accessToken)

        val fromAccount = mockAccounts.find { it.accountNumber == request.fromAccountNumber }
            ?: throw ApiException(404, "Source account not found")
        val toAccount = mockAccounts.find { it.accountNumber == request.toAccountNumber }
            ?: throw ApiException(404, "Destination account not found")

        if (fromAccount.balance < request.amount) {
            throw ApiException(400, "Insufficient funds")
        }

        // Update balances
        val fromIndex = mockAccounts.indexOf(fromAccount)
        val toIndex = mockAccounts.indexOf(toAccount)
        mockAccounts[fromIndex] = fromAccount.copy(balance = fromAccount.balance - request.amount)
        mockAccounts[toIndex] = toAccount.copy(balance = toAccount.balance + request.amount)

        // Create transactions
        mockTransactions.add(
            TransactionDto(
                id = transactionIdCounter++,
                accountId = fromAccount.id,
                amount = request.amount,
                type = "TRANSFER",
                direction = "DEBIT",
                timestamp = "2026-02-18T${Clock.System.now().toEpochMilliseconds() % 24}:00:00Z",
                description = "Transfer to ${request.toAccountNumber}",
                relatedAccountNumber = request.toAccountNumber
            )
        )
        mockTransactions.add(
            TransactionDto(
                id = transactionIdCounter++,
                accountId = toAccount.id,
                amount = request.amount,
                type = "TRANSFER",
                direction = "CREDIT",
                timestamp = "2026-02-18T${Clock.System.now().toEpochMilliseconds() % 24}:00:00Z",
                description = "Transfer from ${request.fromAccountNumber}",
                relatedAccountNumber = request.fromAccountNumber
            )
        )
    }

    override suspend fun getTransactions(
        accountId: Long,
        page: Int,
        size: Int,
        accessToken: String
    ): PaginatedResponse<TransactionDto> {
        delay(600) // Simulate network delay
        validateToken(accessToken)

        val accountTransactions = mockTransactions.filter { it.accountId == accountId }
            .sortedByDescending { it.timestamp }

        val startIndex = page * size
        val endIndex = minOf(startIndex + size, accountTransactions.size)
        val pageContent = if (startIndex < accountTransactions.size) {
            accountTransactions.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PaginatedResponse(
            content = pageContent,
            totalPages = (accountTransactions.size + size - 1) / size,
            totalElements = accountTransactions.size.toLong(),
            number = page,
            size = size,
            first = page == 0,
            last = endIndex >= accountTransactions.size
        )
    }

    private fun validateToken(token: String) {
        if (!token.startsWith("mock_access_token_")) {
            throw ApiException(401, "Invalid token")
        }
        // Check if access token is expired (5 minutes)
        val tokenTimestamp = token.removePrefix("mock_access_token_").toLongOrNull()
        if (tokenTimestamp != null) {
            val now = Clock.System.now().toEpochMilliseconds()
            if (now - tokenTimestamp > ACCESS_TOKEN_VALIDITY_MS) {
                throw ApiException(401, "Access token expired")
            }
        }
    }

    private fun generateAccountNumber(): String {
        return (1000000000L + Random.nextLong(9000000000L)).toString()
    }

    private data class MockUser(
        val id: Long,
        val username: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val phoneNumber: String
    )
}

class ApiException(val code: Int, message: String) : Exception(message)

