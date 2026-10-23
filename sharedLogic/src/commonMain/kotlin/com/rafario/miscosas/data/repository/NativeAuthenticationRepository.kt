package com.rafario.miscosas.data.repository

import com.rafario.miscosas.api.auth.NativeAuthenticationProvider
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository

internal class NativeAuthenticationRepository(
    private val provider: NativeAuthenticationProvider
) : AuthenticationRepository {
    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): UserId {
        TODO("Registrar mediante el proveedor nativo")
    }

    override suspend fun getCurrentUserId(): UserId? {
        val userId = provider.getCurrentUserId() ?: return null
        return UserId(userId)
    }

}