package com.kifiya.banking

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform