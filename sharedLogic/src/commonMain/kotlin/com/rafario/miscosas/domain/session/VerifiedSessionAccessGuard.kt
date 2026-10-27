package com.rafario.miscosas.domain.session

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.repository.AuthenticationRepository

internal class VerifiedSessionAccessGuard(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend fun requireVerifiedSession() {
        when (val session = authenticationRepository.getCurrentSession()) {
            is AuthenticationSessionResult.Authenticated -> {
                if (!session.isEmailVerified) {
                    throw SessionAccessDeniedException()
                }
            }

            AuthenticationSessionResult.NoSession ->
                throw SessionAccessDeniedException()

            AuthenticationSessionResult.RecoverableError ->
                throw SessionAccessStateUnavailableException()
        }
    }
}

internal class SessionAccessDeniedException : IllegalStateException(
    "A verified session is required to access protected household and item data",
)

internal class SessionAccessStateUnavailableException : IllegalStateException(
    "Could not verify the current session before accessing protected data",
)
