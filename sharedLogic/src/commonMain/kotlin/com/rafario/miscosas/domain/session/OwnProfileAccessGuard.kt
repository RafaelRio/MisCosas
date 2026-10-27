package com.rafario.miscosas.domain.session

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository

internal class OwnProfileAccessGuard(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend fun requireOwnProfileAccess(userId: UserId) {
        when (val session = authenticationRepository.getCurrentSession()) {
            is AuthenticationSessionResult.Authenticated -> {
                if (session.userId != userId) {
                    throw ProfileAccessDeniedException()
                }
            }

            AuthenticationSessionResult.NoSession ->
                throw ProfileAccessDeniedException()

            AuthenticationSessionResult.RecoverableError ->
                throw SessionAccessStateUnavailableException()
        }
    }
}

internal class ProfileAccessDeniedException : IllegalStateException(
    "The current session can only access its own profile",
)
