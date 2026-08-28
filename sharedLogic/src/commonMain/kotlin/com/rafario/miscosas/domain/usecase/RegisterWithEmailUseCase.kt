package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import kotlin.coroutines.cancellation.CancellationException

internal class RegisterWithEmailUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val createUserUseCase: CreateUserUseCase,
) {
    suspend operator fun invoke(
        displayName: String,
        email: String,
        password: String,
    ): UserId {
        require(displayName.isNotBlank()) {
            "User displayName must not be blank"
        }
        val userId = authenticationRepository.registerWithEmail(
            email = email,
            password = password,
        )
        try {
            createUserUseCase(
                userId = userId,
                displayName = displayName,
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            throw RegistrationPartiallyCompletedException(
                authenticatedUserId = userId,
                displayName = displayName,
                cause = exception,
            )
        }
        return userId
    }
}
