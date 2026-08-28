package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
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
        assertEquals(1, fakeAuthenticationRepository.registerCalls)
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
        assertEquals(0, fakeAuthenticationRepository.registerCalls)
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
        assertEquals(1, failingAuthenticationRepository.registerCalls)
        assertNull(fakeUserRepository.savedUser)
    }

    @Test
    fun reportsPartiallyCompletedRegistrationWhenLocalProfileFails() = runTest {
        val userId = UserId("firebase-user-123")
        val name = "Rafa"
        val email = "rafael@example.com"
        val password = "123456"
        val fakeAuthenticationRepository = FakeAuthenticationRepository(
            userIdToReturn = userId,
        )
        val instant = Instant.parse("2020-08-30T18:43:00.000000400Z")
        val localFailure = TestLocalProfileException()

        var clockCalls = 0
        val clock = object : Clock {
            override fun now(): Instant {
                clockCalls++
                return instant
            }
        }
        val failingUserRepository = FailingUserRepository(
            failure = localFailure,
        )

        val createUserUseCase = CreateUserUseCase(
            userRepository = failingUserRepository,
            clock = clock,
        )

        val failure = assertFailsWith<RegistrationPartiallyCompletedException> {
            RegisterWithEmailUseCase(
                authenticationRepository = fakeAuthenticationRepository,
                createUserUseCase = createUserUseCase,
            ).invoke(
                displayName = name,
                email = email,
                password = password,
            )
        }

        assertEquals(userId, failure.authenticatedUserId)
        assertEquals(name, failure.displayName)
        assertSame(localFailure, failure.cause)
        assertEquals(1, fakeAuthenticationRepository.registerCalls)
        assertEquals(email, fakeAuthenticationRepository.receivedEmail)
        assertEquals(password, fakeAuthenticationRepository.receivedPassword)
        assertEquals(1, clockCalls)
        assertEquals(1, failingUserRepository.saveCalls)
        assertEquals(
            User(
                id = userId,
                displayName = name,
                createdAt = instant,
                updatedAt = instant,
            ),
            failingUserRepository.savedUser,
        )
    }

    @Test
    fun doesNotWrapCancellationWhileCreatingLocalProfile() = runTest {
        val userId = UserId("firebase-user-123")
        val cancellation = CancellationException("Test cancellation")
        val fakeAuthenticationRepository = FakeAuthenticationRepository(
            userIdToReturn = userId,
        )
        val failingUserRepository = FailingUserRepository(
            failure = cancellation,
        )
        val createUserUseCase = CreateUserUseCase(
            userRepository = failingUserRepository,
            clock = object : Clock {
                override fun now(): Instant =
                    Instant.parse("2020-08-30T18:43:00.000000400Z")
            },
        )

        val propagatedCancellation = assertFailsWith<CancellationException> {
            RegisterWithEmailUseCase(
                authenticationRepository = fakeAuthenticationRepository,
                createUserUseCase = createUserUseCase,
            ).invoke(
                displayName = "Rafa",
                email = "rafael@example.com",
                password = "123456",
            )
        }

        assertSame(cancellation, propagatedCancellation)
        assertEquals(1, fakeAuthenticationRepository.registerCalls)
    }

    private class FakeAuthenticationRepository(
        private val userIdToReturn: UserId,
    ) : AuthenticationRepository {

        var receivedEmail: String? = null
            private set

        var receivedPassword: String? = null
            private set

        var registerCalls: Int = 0
            private set

        override suspend fun registerWithEmail(
            email: String,
            password: String,
        ): UserId {
            registerCalls++
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

        var registerCalls: Int = 0
            private set

        override suspend fun registerWithEmail(
            email: String,
            password: String,
        ): UserId {
            registerCalls++
            receivedEmail = email
            receivedPassword = password
            throw TestAuthenticationException()
        }
    }

    private class TestAuthenticationException : Exception()
    private class TestLocalProfileException : Exception()

    private class RecordingUserRepository : UserRepository {

        var savedUser: User? = null
            private set

        override suspend fun save(user: User) {
            savedUser = user
        }
    }

    private class FailingUserRepository(
        private val failure: Exception,
    ) : UserRepository {

        var savedUser: User? = null
            private set

        var saveCalls: Int = 0
            private set

        override suspend fun save(user: User) {
            saveCalls++
            savedUser = user
            throw failure
        }
    }
}
