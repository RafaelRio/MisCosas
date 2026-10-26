package com.rafario.miscosas.domain.model

sealed interface SessionState {
    data object SignedOut: SessionState
    data class ProfilePending(val userId: UserId): SessionState
    data class Ready(val user: User): SessionState
    data object Restoring: SessionState
    data class Unverified(val userId: UserId): SessionState
    data object RecoverableError: SessionState
}