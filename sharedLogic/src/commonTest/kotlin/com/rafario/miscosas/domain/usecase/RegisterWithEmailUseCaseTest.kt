package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
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
    fun trimsOuterWhitespaceFromDisplayNameBeforeSavingProfile() = runTest {
        val userId = UserId("firebase-user-trimmed-name")
        val fakeAuthenticationRepository = FakeAuthenticationRepository(
            userIdToReturn = userId,
        )
        val fakeUserRepository = RecordingUserRepository()
        val instant = Instant.parse("2026-08-30T18:43:00.000000400Z")
        val clock = object : Clock {
            override fun now(): Instant = instant
        }
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = fakeAuthenticationRepository,
            createUserUseCase = CreateUserUseCase(
                userRepository = fakeUserRepository,
                clock = clock,
            ),
        )

        useCase(
            displayName = "  Ana María  ",
            email = "ana@example.com",
            password = "clave-test-123",
        )

        assertEquals("Ana María", fakeUserRepository.savedUser?.displayName)
        assertEquals(1, fakeAuthenticationRepository.registerCalls)
    }

    @Test
    fun checkLengthFromDisplayNameBeforeSavingProfile() = runTest {
        val userId = UserId("firebase-user-trimmed-name")
        val fakeAuthenticationRepository = FakeAuthenticationRepository(
            userIdToReturn = userId,
        )
        val fakeUserRepository = RecordingUserRepository()
        val instant = Instant.parse("2026-08-30T18:43:00.000000400Z")
        val clock = object : Clock {
            override fun now(): Instant = instant
        }
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = fakeAuthenticationRepository,
            createUserUseCase = CreateUserUseCase(
                userRepository = fakeUserRepository,
                clock = clock,
            ),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(
                displayName = "A".repeat(101),
                email = "ana@example.com",
                password = "clave-test-123",
            )
        }

        assertEquals(0, fakeAuthenticationRepository.registerCalls)
    }

    @Test
    fun acceptsDisplayNameAtMaximumLengthAfterTrimmingOuterSpaces() = runTest {
        val userId = UserId("firebase-user-maximum-name")
        val authenticationRepository = FakeAuthenticationRepository(userId)
        val userRepository = RecordingUserRepository()
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = authenticationRepository,
            createUserUseCase = CreateUserUseCase(userRepository),
        )
        val name = "A".repeat(100)

        useCase(
            displayName = "  $name  ",
            email = "ana@example.com",
            password = "12345678",
        )

        assertEquals(name, userRepository.savedUser?.displayName)
        assertEquals(1, authenticationRepository.registerCalls)
    }

    @Test
    fun acceptsSingleCharacterInternationalDisplayName() = runTest {
        val authenticationRepository = FakeAuthenticationRepository(UserId("user-single-name"))
        val userRepository = RecordingUserRepository()
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = authenticationRepository,
            createUserUseCase = CreateUserUseCase(userRepository),
        )

        useCase(
            displayName = "李",
            email = "li@example.com",
            password = "12345678",
        )

        assertEquals("李", userRepository.savedUser?.displayName)
        assertEquals(1, authenticationRepository.registerCalls)
    }

    @Test
    fun trimsEmailButPreservesCaseBeforeAuthentication() = runTest {
        val userId = UserId("firebase-user-normalized-email")
        val authenticationRepository = FakeAuthenticationRepository(userId)
        val userRepository = RecordingUserRepository()
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = authenticationRepository,
            createUserUseCase = CreateUserUseCase(userRepository),
        )

        useCase(
            displayName = "Ana",
            email = "  Ana.Example@Example.COM  ",
            password = " 1234567 ",
        )

        assertEquals("Ana.Example@Example.COM", authenticationRepository.receivedEmail)
        assertEquals(" 1234567 ", authenticationRepository.receivedPassword)
        assertEquals(1, authenticationRepository.registerCalls)
    }

    @Test
    fun rejectsEmailWithoutTextOnBothSidesOfAtBeforeAuthentication() = runTest {
        val authenticationRepository = FakeAuthenticationRepository(UserId("unused-user"))
        val userRepository = RecordingUserRepository()
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = authenticationRepository,
            createUserUseCase = CreateUserUseCase(userRepository),
        )

        for (email in listOf("", "   ", "ana.example.com", "@example.com", "ana@")) {
            assertFailsWith<IllegalArgumentException>(email) {
                useCase(
                    displayName = "Ana",
                    email = email,
                    password = "12345678",
                )
            }
        }

        assertEquals(0, authenticationRepository.registerCalls)
        assertNull(authenticationRepository.receivedEmail)
        assertNull(userRepository.savedUser)
    }

    @Test
    fun rejectsPasswordShorterThanEightCharactersBeforeAuthentication() = runTest {
        val authenticationRepository = FakeAuthenticationRepository(UserId("unused-user"))
        val userRepository = RecordingUserRepository()
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = authenticationRepository,
            createUserUseCase = CreateUserUseCase(userRepository),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(
                displayName = "Ana",
                email = "ana@example.com",
                password = "1234567",
            )
        }

        assertEquals(0, authenticationRepository.registerCalls)
        assertNull(authenticationRepository.receivedEmail)
        assertNull(userRepository.savedUser)
    }

    @Test
    fun registersWithAuthThenCreatesLocalUserProfile() = runTest {
        val name = "Rafael"
        val email = "rafael@example.com"
        val password = "12345678"
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
    fun rejectsZeroWidthSpaceOnlyDisplayNameBeforeAuthentication() = runTest {
        val fakeAuthenticationRepository = FakeAuthenticationRepository(
            userIdToReturn = UserId("firebase-user-invisible-name"),
        )
        val fakeUserRepository = RecordingUserRepository()
        val useCase = RegisterWithEmailUseCase(
            authenticationRepository = fakeAuthenticationRepository,
            createUserUseCase = CreateUserUseCase(fakeUserRepository),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(
                displayName = "\u200B",
                email = "ana@example.com",
                password = "clave-test-123",
            )
        }

        assertEquals(0, fakeAuthenticationRepository.registerCalls)
        assertNull(fakeUserRepository.savedUser)
    }

    @Test
    fun rejectsBlankDisplayNameBeforeRegisteringRemoteAccount() = runTest {
        val name = "    "
        val email = "rafael@example.com"
        val password = "12345678"
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
        val password = "12345678"

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
        val password = "12345678"
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
                password = "12345678",
            )
        }

        assertSame(cancellation, propagatedCancellation)
        assertEquals(1, fakeAuthenticationRepository.registerCalls)
    }

    @Test
    fun retriesProfileAfterPartialRegistrationWithoutRegisteringAgain() = runTest {
        val userId = UserId("firebase-user-123")
        val name = "  Rafael  "
        val email = "rafael@example.com"
        val password = "12345678"
        val instant = Instant.parse("2026-08-30T18:43:00.000000400Z")
        val localFailure = TestLocalProfileException()
        val authenticationRepository = FakeAuthenticationRepository(userId)
        val userRepository = SecondTryUserRepository(localFailure)
        val createUserUseCase = CreateUserUseCase(
            userRepository = userRepository,
            clock = object : Clock {
                override fun now(): Instant = instant
            },
        )
        val registerUseCase = RegisterWithEmailUseCase(
            authenticationRepository = authenticationRepository,
            createUserUseCase = createUserUseCase,
        )

        val partialFailure = assertFailsWith<RegistrationPartiallyCompletedException> {
            registerUseCase(
                displayName = name,
                email = email,
                password = password,
            )
        }

        assertEquals(userId, partialFailure.authenticatedUserId)
        assertEquals("Rafael", partialFailure.displayName)
        assertSame(localFailure, partialFailure.cause)
        assertEquals(1, authenticationRepository.registerCalls)

        val completeProfileUseCase = CompleteUserProfileUseCase(
            userRepository = userRepository,
            createUserUseCase = createUserUseCase,
        )
        completeProfileUseCase(
            userId = partialFailure.authenticatedUserId,
            displayName = partialFailure.displayName,
        )

        assertEquals(listOf(userId), userRepository.requestedUserIds)
        assertEquals(2, userRepository.saveCalls)
        assertEquals(1, authenticationRepository.registerCalls)
        assertEquals(
            User(
                id = userId,
                displayName = "Rafael",
                createdAt = instant,
                updatedAt = instant,
            ),
            userRepository.savedUser,
        )

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

        override suspend fun signInWithEmail(
            email: String,
            password: String
        ): UserId {
            return UserId("native-user-id")
        }

        override suspend fun getCurrentSession(): AuthenticationSessionResult {
            error("No se debe consultar la sesión durante el registro")
        }

        override suspend fun sendEmailVerification() {

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

        override suspend fun signInWithEmail(
            email: String,
            password: String
        ): UserId {
            return UserId("native-user-id")
        }

        override suspend fun getCurrentSession(): AuthenticationSessionResult {
            error("No se debe consultar la sesión durante el registro")
        }

        override suspend fun sendEmailVerification() {

        }
    }

    private class TestAuthenticationException : Exception()
    private class TestLocalProfileException : Exception()
    private class RecordingUserRepository : UserRepository {

        override suspend fun findById(userId: UserId): User? {
            error("Unexpected findById call")
        }

        var savedUser: User? = null
            private set

        override suspend fun save(user: User) {
            savedUser = user
        }
    }

    private class FailingUserRepository(
        private val failure: Exception,
    ) : UserRepository {

        override suspend fun findById(userId: UserId): User? {
            error("Unexpected findById call")
        }

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

    private class SecondTryUserRepository(
        private val firstSaveFailure: Exception,
    ) : UserRepository {
        val requestedUserIds = mutableListOf<UserId>()

        var savedUser: User? = null
            private set

        override suspend fun findById(userId: UserId): User? {
            requestedUserIds.add(userId)
            return savedUser?.takeIf { it.id == userId }
        }

        var saveCalls: Int = 0
            private set

        override suspend fun save(user: User) {
            saveCalls++
            if (saveCalls == 1) {
                throw firstSaveFailure
            } else {
                savedUser = user
            }
        }
    }
}
