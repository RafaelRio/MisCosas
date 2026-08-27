package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository

internal class RegisterWithEmailUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val createUserUseCase: CreateUserUseCase,
) {
    suspend operator fun invoke(
        displayName: String,
        email: String,
        password: String,
    ): UserId {
        val userId = authenticationRepository.registerWithEmail(
            email = email,
            password = password,
        )
        createUserUseCase(
            userId = userId,
            displayName = displayName,
        )
        return userId
    }
}
