package com.kifiya.banking.data.local

import com.kifiya.banking.domain.model.AuthTokens
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.invoke
import kotlin.time.Clock

class TokenStorage(private val kSafe: KSafe) {

    private var accessToken by kSafe("")
    private var refreshToken by kSafe("")
    private var expiresIn by kSafe(0L)
    private var tokenSavedAt by kSafe(0L)

    fun saveTokens(tokens: AuthTokens) {
        accessToken = tokens.accessToken
        refreshToken = tokens.refreshToken
        expiresIn = tokens.expiresIn
        tokenSavedAt = Clock.System.now().toEpochMilliseconds()
    }

    fun getTokens(): AuthTokens? {
        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            return null
        }
        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiresIn
        )
    }

    fun getAccessTokenOrNull(): String? {
        return accessToken.takeIf { it.isNotEmpty() }
    }

    fun getRefreshTokenOrNull(): String? {
        return refreshToken.takeIf { it.isNotEmpty() }
    }

    fun isTokenExpired(): Boolean {
        if (accessToken.isEmpty()) return true
        val now = Clock.System.now().toEpochMilliseconds()
        val expiryTime = tokenSavedAt + (expiresIn * 1000) // expiresIn is in seconds
        return now >= expiryTime
    }

    fun clearTokens() {
        accessToken = ""
        refreshToken = ""
        expiresIn = 0L
        tokenSavedAt = 0L
    }

    fun isLoggedIn(): Boolean {
        return accessToken.isNotEmpty()
    }
}
