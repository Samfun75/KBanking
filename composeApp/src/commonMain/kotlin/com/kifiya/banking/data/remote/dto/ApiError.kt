package com.kifiya.banking.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val message: String,
    val code: String? = null
)

