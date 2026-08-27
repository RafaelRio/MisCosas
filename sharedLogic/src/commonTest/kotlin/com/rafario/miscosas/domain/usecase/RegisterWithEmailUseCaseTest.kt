package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
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

    @Test
    fun rejectsBlankDisplayNameBeforeRegisteringRemoteAccount() = runTest {
        val name = "    "
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

        assertFailsWith<IllegalArgumentException> {
            RegisterWithEmailUseCase(
                authenticationRepository = fakeAuthenticationRepository,
                createUserUseCase = createUserUseCase,
            ).invoke(
                displayName = name,
                email = email,
                password = password,
            )
        }

        assertNull(fakeAuthenticationRepository.receivedEmail)
        assertNull(fakeAuthenticationRepository.receivedPassword)
        assertNull(fakeUserRepository.savedUser)
    }

    @Test
    fun doesNotCreateLocalProfileWhenAuthenticationFails() = runTest {
        val name = "Rafa"
        val email = "rafael@example.com"
        val password = "123456"

        val instant = Instant.parse("2020-08-30T18:43:00.000000400Z")

        val failingAuthenticationRepository = FailingAuthenticationRepository()

        val fakeUserRepository = RecordingUserRepository()

        val clock = object : Clock {
            override fun now(): Instant = instant
        }

        val createUserUseCase = CreateUserUseCase(
            userRepository = fakeUserRepository,
            clock = clock,
        )

        assertFailsWith<TestAuthenticationException> {
            RegisterWithEmailUseCase(
                authenticationRepository = failingAuthenticationRepository,
                createUserUseCase = createUserUseCase,
            ).invoke(
                displayName = name,
                email = email,
                password = password,
            )
        }

        assertEquals(email, failingAuthenticationRepository.receivedEmail)
        assertEquals(password, failingAuthenticationRepository.receivedPassword)
        assertNull(fakeUserRepository.savedUser)
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

    private class FailingAuthenticationRepository : AuthenticationRepository {

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
            throw TestAuthenticationException()
        }
    }

    private class TestAuthenticationException : Exception()

    private class RecordingUserRepository : UserRepository {

        var savedUser: User? = null
            private set

        override suspend fun save(user: User) {
            savedUser = user
        }
    }
}
