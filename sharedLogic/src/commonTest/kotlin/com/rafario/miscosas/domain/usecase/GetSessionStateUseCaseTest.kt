package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.time.Instant

class GetSessionStateUseCaseTest {

    @Test
    fun returnsSignedOutWhenThereIsNoAuthenticatedUser() = runTest {
        val repository = FakeAuthenticationRepository()
        val userRepository = FakeUserRepository()
        val useCase = GetSessionStateUseCase(
            repository = repository, userRepository = userRepository
        )

        val result = useCase()

        assertEquals(SessionState.SignedOut, result)
    }

    @Test
    fun returnsProfilePendingWhenAuthenticatedUserHasNoLocalProfile() = runTest {
        val userId = UserId("user-123")
        val repository = FakeUserRepository()
        val sessionRepository = FakeAuthenticationRepository(
            AuthenticationSessionResult.Authenticated(
                userId = userId, isEmailVerified = false
            )
        )
        val useCase =
            GetSessionStateUseCase(repository = sessionRepository, userRepository = repository)

        val result = useCase()

        assertEquals(SessionState.ProfilePending(userId), result)
    }

    @Test
    fun returnsReadyWhenAuthenticatedUserHasLocalProfile() = runTest {
        val user = User(
            id = UserId("firebase-user-123"),
            displayName = "Rafael Río",
            createdAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
            updatedAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
        )
        val repository = FakeUserRepository(user)
        val sessionRepository = FakeAuthenticationRepository(
            AuthenticationSessionResult.Authenticated(
                userId = user.id, isEmailVerified = true
            )
        )
        val useCase = GetSessionStateUseCase(
            repository = sessionRepository, userRepository = repository
        )

        val result = useCase()

        assertEquals(SessionState.Ready(user), result)
    }

    @Test
    fun returnsUnverifiedWhenAuthenticatedUserHasLocalProfileButEmailIsNotVerified() = runTest {
        val userId = UserId("firebase-user-unverified")
        val user = User(
            id = userId,
            displayName = "Usuario no verificado",
            createdAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
            updatedAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
        )
        val sessionRepository = FakeAuthenticationRepository(
            AuthenticationSessionResult.Authenticated(
                userId = userId,
                isEmailVerified = false,
            )
        )
        val useCase = GetSessionStateUseCase(
            repository = sessionRepository,
            userRepository = FakeUserRepository(user),
        )

        val result = useCase()

        assertEquals(SessionState.Unverified(userId), result)
    }

    @Test
    fun returnsRecoverableErrorWhenSessionLookupReportsRecoverableError() = runTest {
        val useCase = GetSessionStateUseCase(
            repository = FakeAuthenticationRepository(AuthenticationSessionResult.RecoverableError),
            userRepository = FakeUserRepository(),
        )

        val result = useCase()

        assertEquals(SessionState.RecoverableError, result)
    }

    @Test
    fun propagatesSessionLookupFailureInsteadOfReturningSignedOut() = runTest {
        val exception = IllegalStateException("No se pudo consultar la sesión")
        val authenticationRepository = object : AuthenticationRepository {

            override suspend fun registerWithEmail(
                email: String,
                password: String,
            ): UserId {
                error("No se debe registrar una cuenta al consultar la sesión")
            }

            override suspend fun signInWithEmail(
                email: String,
                password: String,
            ): UserId {
                error("No se debe iniciar sesión al consultar el estado de sesión")
            }

            override suspend fun getCurrentSession(): AuthenticationSessionResult {
                throw exception
            }
        }
        val useCase = GetSessionStateUseCase(
            repository = authenticationRepository,
            userRepository = FakeUserRepository(),
        )

        val caught = assertFailsWith<IllegalStateException> {
            useCase()
        }

        assertSame(exception, caught)
    }

    @Test
    fun propagatesCancellationWhenSessionLookupIsCancelled() = runTest {
        val cancellation = CancellationException("Consulta de sesión cancelada")
        val authenticationRepository = object : AuthenticationRepository {
            override suspend fun getCurrentSession(): AuthenticationSessionResult {
                throw cancellation
            }

            override suspend fun registerWithEmail(
                email: String,
                password: String,
            ): UserId {
                error("No se debe registrar una cuenta al consultar la sesión")
            }

            override suspend fun signInWithEmail(
                email: String,
                password: String,
            ): UserId {
                error("No se debe iniciar sesión al consultar el estado de sesión")
            }
        }
        val useCase = GetSessionStateUseCase(
            repository = authenticationRepository,
            userRepository = FakeUserRepository(),
        )

        val caught = assertFailsWith<CancellationException> {
            useCase()
        }

        assertSame(cancellation, caught)
    }

    @Test
    fun propagatesLocalProfileLookupFailureInsteadOfReturningProfilePending() = runTest {
        val userId = UserId("firebase-user-unverified")
        val exception = IllegalStateException("Fallo de lectura")

        val repository = FakeAuthenticationRepository(
            result = AuthenticationSessionResult.Authenticated(
                userId = userId,
                isEmailVerified = false
            )
        )

        val userRepository = FakeUserExceptionRepository(exception)

        val useCase = GetSessionStateUseCase(
            repository = repository,
            userRepository = userRepository
        )

        val caught = assertFailsWith<IllegalStateException> {
            useCase()
        }

        assertSame(exception, caught)
    }

    private class FakeAuthenticationRepository(
        private val result: AuthenticationSessionResult = AuthenticationSessionResult.NoSession,
    ) :
        AuthenticationRepository {

        override suspend fun registerWithEmail(
            email: String, password: String
        ): UserId {
            error("No se debe registrar una cuenta al consultar la sesión")
        }

        override suspend fun signInWithEmail(
            email: String,
            password: String,
        ): UserId {
            error("No se debe iniciar sesión al consultar el estado de sesión")
        }

        override suspend fun getCurrentSession(): AuthenticationSessionResult {
            return result
        }
    }

    private class FakeUserRepository(val user: User? = null) : UserRepository {

        override suspend fun findById(userId: UserId): User? =
            if (user?.id == userId) user else null

        override suspend fun save(user: User) {
            error("consultar el estado no debe guardar nada.")
        }
    }

    private class FakeUserExceptionRepository(val exception: Exception): UserRepository {

        override suspend fun findById(userId: UserId): User? =
            throw exception

        override suspend fun save(user: User) {
            error("consultar el estado no debe guardar nada.")
        }
    }
}
