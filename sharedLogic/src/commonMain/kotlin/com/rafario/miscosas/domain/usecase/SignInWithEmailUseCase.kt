package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository

internal class SignInWithEmailUseCase(
    private val repository: AuthenticationRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): UserId {
        val normalizedEmail = email.trim()
        val atIndex = normalizedEmail.indexOf('@')

        require(atIndex > 0 && atIndex < normalizedEmail.lastIndex) {
            "Email no válido"
        }

        require(password.isNotEmpty()) {
            "La contraseña no puede estar vacía"
        }

        return repository.signInWithEmail(
            email = normalizedEmail,
            password = password,
        )
    }
}