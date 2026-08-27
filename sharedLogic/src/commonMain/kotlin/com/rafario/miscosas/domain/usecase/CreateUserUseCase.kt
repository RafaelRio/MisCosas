package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.UserRepository
import kotlin.time.Clock

internal class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(
        userId: UserId,
        displayName: String,
    ) {
        val now = clock.now()
        val user = User(
            id = userId,
            displayName = displayName,
            createdAt = now,
            updatedAt = now,
        )
        userRepository.save(user)
    }
}
