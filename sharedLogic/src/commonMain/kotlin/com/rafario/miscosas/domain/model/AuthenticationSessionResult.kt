package com.rafario.miscosas.domain.model

sealed interface AuthenticationSessionResult {
    data object NoSession : AuthenticationSessionResult

    data class Authenticated(
        val userId: UserId,
        val isEmailVerified: Boolean,
    ) : AuthenticationSessionResult

    data object RecoverableError : AuthenticationSessionResult
}