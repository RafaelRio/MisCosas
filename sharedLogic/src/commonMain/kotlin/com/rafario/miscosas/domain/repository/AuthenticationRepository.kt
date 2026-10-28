package com.rafario.miscosas.domain.repository

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.UserId

internal interface AuthenticationRepository {
    suspend fun registerWithEmail(
        email: String,
        password: String,
    ): UserId

    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): UserId

    suspend fun getCurrentSession(): AuthenticationSessionResult

    suspend fun sendEmailVerification()
}
