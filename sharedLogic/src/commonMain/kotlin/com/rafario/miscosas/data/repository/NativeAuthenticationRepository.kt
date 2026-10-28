package com.rafario.miscosas.data.repository

import com.rafario.miscosas.api.auth.NativeAuthenticationProvider
import com.rafario.miscosas.api.auth.NativeSessionLookupResult
import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository

internal class NativeAuthenticationRepository(
    private val provider: NativeAuthenticationProvider
) : AuthenticationRepository {
    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): UserId {
        return UserId(provider.registerWithEmail(email, password))
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ) = UserId(provider.signInWithEmail(email, password))


    override suspend fun getCurrentSession(): AuthenticationSessionResult {
        return when (val nativeResult = provider.getCurrentSession()) {
            is NativeSessionLookupResult.Authenticated -> AuthenticationSessionResult.Authenticated(
                userId = UserId(nativeResult.userId),
                isEmailVerified = nativeResult.isEmailVerified
            )
            NativeSessionLookupResult.NoSession -> AuthenticationSessionResult.NoSession
            NativeSessionLookupResult.RecoverableError -> AuthenticationSessionResult.RecoverableError
        }
    }

    override suspend fun sendEmailVerification() {
        provider.sendEmailVerification()
    }
}