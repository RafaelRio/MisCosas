package com.rafario.miscosas.domain.model

internal sealed interface SessionState {
    data object SignedOut: SessionState
    data class ProfilePending(val userId: UserId): SessionState
    data class Ready(val user: User): SessionState
}