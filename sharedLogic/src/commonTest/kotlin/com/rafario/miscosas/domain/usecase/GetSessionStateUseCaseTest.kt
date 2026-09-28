package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class GetSessionStateUseCaseTest {

    @Test
    fun returnsSignedOutWhenThereIsNoAuthenticatedUser() = runTest {
        val repository = FakeGetUserIdRepository()
        val userRepository = FakeUserRepository()
        val useCase = GetSessionStateUseCase(
            repository = repository,
            userRepository = userRepository
        )

        val result = useCase()

        assertEquals(SessionState.SignedOut, result)
    }

    @Test
    fun returnsProfilePendingWhenAuthenticatedUserHasNoLocalProfile() = runTest {
        val userId = UserId("user-123")
        val repository = FakeUserRepository()
        val sessionRepository = FakeGetUserIdRepository(userId)
        val useCase = GetSessionStateUseCase(
            repository = sessionRepository,
            userRepository = repository
        )

        val result = useCase()

        assertEquals(SessionState.ProfilePending(userId), result)
    }

    @Test
    fun returnsReadyWhenAuthenticatedUserHasLocalProfile() = runTest {
        val user = User(
            id = UserId("firebase-user-123"),
            displayName = "Rafael Río",
            createdAt =
                Instant.parse("2026-08-27T09:00:00.000000500Z"),
            updatedAt =
                Instant.parse("2026-08-27T09:00:00.000000500Z"),
        )
        val repository = FakeUserRepository(user)
        val sessionRepository = FakeGetUserIdRepository(user.id)
        val useCase = GetSessionStateUseCase(
            repository = sessionRepository,
            userRepository = repository
        )

        val result = useCase()

        assertEquals(SessionState.Ready(user), result)
    }

    private class FakeGetUserIdRepository(val userId: UserId? = null): AuthenticationRepository {

        override suspend fun getCurrentUserId(): UserId? = userId

        override suspend fun registerWithEmail(
            email: String,
            password: String
        ): UserId {
            error("No se debe registrar una cuenta al consultar la sesión")
        }
    }

    private class FakeUserRepository(val user: User? = null): UserRepository {

        override suspend fun findById(userId: UserId): User? =
            if (user?.id == userId) user else null

        override suspend fun save(user: User) {
            error("consultar el estado no debe guardar nada.")
        }


    }
}