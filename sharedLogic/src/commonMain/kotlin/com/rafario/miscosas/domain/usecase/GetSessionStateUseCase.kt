package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository

internal class GetSessionStateUseCase(
    private val repository: AuthenticationRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): SessionState {
        return when (val session = repository.getCurrentSession()) {
            is AuthenticationSessionResult.Authenticated -> {
                val user = userRepository.findById(userId = session.userId)
                when {
                    user == null ->
                        SessionState.ProfilePending(session.userId)

                    !session.isEmailVerified ->
                        SessionState.Unverified(session.userId)

                    else ->
                        SessionState.Ready(user)
                }

            }

            AuthenticationSessionResult.NoSession -> SessionState.SignedOut
            AuthenticationSessionResult.RecoverableError -> SessionState.RecoverableError
        }
    }
}