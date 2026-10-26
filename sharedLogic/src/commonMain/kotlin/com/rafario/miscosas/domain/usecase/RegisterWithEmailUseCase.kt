package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlin.text.CharCategory

internal class RegisterWithEmailUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val createUserUseCase: CreateUserUseCase,
) {
    suspend operator fun invoke(
        displayName: String,
        email: String,
        password: String,
    ): UserId {
        val name = displayName.trim()
        val normalizedEmail = email.trim()
        require(name.any { character ->
            !character.isWhitespace() &&
                character.category != CharCategory.FORMAT &&
                character.category != CharCategory.CONTROL
        }) {
            "User displayName must contain visible characters"
        }
        require(name.length <= 100) {
            "User displayName must be at most 100 characters"
        }
        val atIndex = normalizedEmail.indexOf('@')
        require(atIndex > 0 && atIndex < normalizedEmail.lastIndex) {
            "Email must contain text before and after @"
        }
        require(password.length >= 8) {
            "Password must be at least 8 characters"
        }
        val userId = authenticationRepository.registerWithEmail(
            email = normalizedEmail,
            password = password,
        )
        try {
            createUserUseCase(
                userId = userId,
                displayName = name,
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            throw RegistrationPartiallyCompletedException(
                authenticatedUserId = userId,
                displayName = name,
                cause = exception,
            )
        }
        return userId
    }
}
