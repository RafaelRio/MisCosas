package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepositoryProvider

internal class GetSessionStateUseCase(
    private val repository: AuthenticationRepository,
    private val userRepositoryProvider: UserRepositoryProvider,
) {
    suspend operator fun invoke(): SessionState {
        return when (val session = repository.getCurrentSession()) {
            is AuthenticationSessionResult.Authenticated -> {
                val userRepository = userRepositoryProvider.forUser(session.userId)
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

            AuthenticationSessionResult.NoSession -> {
                userRepositoryProvider.closeCurrent()
                SessionState.SignedOut
            }
            AuthenticationSessionResult.RecoverableError -> SessionState.RecoverableError
        }
    }
}
