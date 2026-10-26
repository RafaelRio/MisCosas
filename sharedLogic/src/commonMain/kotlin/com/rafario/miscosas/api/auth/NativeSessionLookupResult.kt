package com.rafario.miscosas.api.auth

sealed interface NativeSessionLookupResult {
    data object NoSession : NativeSessionLookupResult

    data class Authenticated(
        val userId: String,
        val isEmailVerified: Boolean,
    ) : NativeSessionLookupResult

    data object RecoverableError : NativeSessionLookupResult
}