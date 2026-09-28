package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository

internal class GetSessionStateUseCase(
    private val repository: AuthenticationRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): SessionState {
        val userId = repository.getCurrentUserId() ?: return SessionState.SignedOut
        val user = userRepository.findById(userId)

        return if (user != null) SessionState.Ready(user)
        else SessionState.ProfilePending(userId)
    }
}