package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.UserRepository

internal class CompleteUserProfileUseCase(
    private val userRepository: UserRepository,
    private val createUserUseCase: CreateUserUseCase
) {
    suspend operator fun invoke(userId: UserId, displayName: String) {
        val user = userRepository.findById(userId)
        if (user != null) {
            return
        }
        createUserUseCase.invoke(userId, displayName)
    }
}