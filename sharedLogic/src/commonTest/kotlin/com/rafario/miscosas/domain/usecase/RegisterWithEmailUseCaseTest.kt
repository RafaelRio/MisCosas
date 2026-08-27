package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class RegisterWithEmailUseCaseTest {

    @Test
    fun registersWithAuthThenCreatesLocalUserProfile() = runTest {
        val name = "Rafael"
        val email = "rafael@example.com"
        val password = "123456"
        val userId = UserId("firebase-user-123")

        val instant = Instant.parse("2020-08-30T18:43:00.000000400Z")

        val fakeAuthenticationRepository = FakeAuthenticationRepository(
            userIdToReturn = userId,
        )
        val fakeUserRepository = RecordingUserRepository()

        val clock = object : Clock {
            override fun now(): Instant = instant
        }

        val createUserUseCase = CreateUserUseCase(
            userRepository = fakeUserRepository,
            clock = clock,
        )

        val registerWithEmailUseCase = RegisterWithEmailUseCase(
            authenticationRepository = fakeAuthenticationRepository,
            createUserUseCase = createUserUseCase,
        )

        val result = registerWithEmailUseCase(
            displayName = name,
            email = email,
            password = password,
        )

        assertEquals(userId, result)
        assertEquals(email, fakeAuthenticationRepository.receivedEmail)
        assertEquals(password, fakeAuthenticationRepository.receivedPassword)
        assertEquals(
            User(
                id = userId,
                displayName = name,
                createdAt = instant,
                updatedAt = instant,
            ),
            fakeUserRepository.savedUser,
        )
    }

    private class FakeAuthenticationRepository(
        private val userIdToReturn: UserId,
    ) : AuthenticationRepository {

        var receivedEmail: String? = null
            private set

        var receivedPassword: String? = null
            private set

        override suspend fun registerWithEmail(
            email: String,
            password: String,
        ): UserId {
            receivedEmail = email
            receivedPassword = password
            return userIdToReturn
        }
    }

    private class RecordingUserRepository : UserRepository {

        var savedUser: User? = null
            private set

        override suspend fun save(user: User) {
            savedUser = user
        }
    }
}
