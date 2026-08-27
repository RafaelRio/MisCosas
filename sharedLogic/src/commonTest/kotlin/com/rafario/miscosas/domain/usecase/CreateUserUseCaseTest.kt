package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class CreateUserUseCaseTest {

    @Test
    fun createsUserWithAuthenticatedIdAndSameInstant() = runTest {
        val fakeRepository = CreateUserFakeRepository()
        val now = Instant.parse("2026-08-26T10:15:30.000000789Z")
        val clock = object : Clock {
            var calls = 0

            override fun now(): Instant {
                calls++
                return now
            }
        }
        val userId = UserId("firebase-user-123")
        val formName = "Rafael"

        val useCase = CreateUserUseCase(
            clock = clock,
            userRepository = fakeRepository,
        )

        useCase(
            userId = userId,
            displayName = formName,
        )

        assertEquals(1, clock.calls)

        val user = User(
            id = userId,
            displayName = formName,
            createdAt = now,
            updatedAt = now,
        )

        assertEquals(user, fakeRepository.savedUser)
    }

    private class CreateUserFakeRepository : UserRepository {

        var savedUser: User? = null
            private set

        override suspend fun save(
            user: User,
        ) {
            savedUser = user
        }
    }
}
