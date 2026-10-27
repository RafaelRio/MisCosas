package com.rafario.miscosas.api.auth

enum class NativeAuthenticationFailure {
    CREDENTIALS_REJECTED,
    TEMPORARY,
    RATE_LIMITED,
    UNEXPECTED,
}

class NativeAuthenticationException(
    val failure: NativeAuthenticationFailure,
    cause: Throwable? = null,
) : Exception(null, cause)