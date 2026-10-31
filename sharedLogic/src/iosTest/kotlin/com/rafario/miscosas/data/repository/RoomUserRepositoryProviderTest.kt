package com.rafario.miscosas.data.repository

import com.rafario.miscosas.data.local.database.createIosUserDatabaseProvider
import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.RemoteUserProfileRepository
import com.rafario.miscosas.domain.usecase.GetSessionStateUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class RoomUserRepositoryProviderTest {

    @Test
    fun switchingAuthenticatedUidUsesAndRestoresEachRoomPartition() = runTest {
        val testRunId = Uuid.random().toString()
        val userAId = UserId("auth-t16-$testRunId-user-a")
        val userBId = UserId("auth-t16-$testRunId-user-b")
        val authenticationRepository = MutableFakeAuthenticationRepository()
        val userRepositoryProvider = RoomUserRepositoryProvider(
            database = createIosUserDatabaseProvider(),
            repository = authenticationRepository,
        )
        val userA = User(
            id = userAId,
            displayName = "Perfil A",
            createdAt = Instant.parse("2026-08-27T09:00:00.000000400Z"),
            updatedAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
        )
        val userB = User(
            id = userBId,
            displayName = "Perfil B",
            createdAt = Instant.parse("2026-08-28T09:00:00.000000400Z"),
            updatedAt = Instant.parse("2026-08-28T09:00:00.000000500Z"),
        )

        try {
            authenticationRepository.currentSession = authenticatedSession(userAId)
            val useCase = GetSessionStateUseCase(
                repository = authenticationRepository,
                userRepositoryProvider = userRepositoryProvider,
                remoteUserProfileRepository = object : RemoteUserProfileRepository {
                    override suspend fun findById(userId: UserId): User? = null

                    override suspend fun upsert(user: User) {
                        error("No se espera escribir en el repositorio remoto")
                    }
                },
            )

            userRepositoryProvider.forUser(userAId).save(userA)
            authenticationRepository.currentSession = authenticatedSession(userBId)
            userRepositoryProvider.forUser(userBId).save(userB)

            authenticationRepository.currentSession = authenticatedSession(userAId)
            assertEquals(SessionState.Ready(userA), useCase())

            authenticationRepository.currentSession = authenticatedSession(userBId)
            assertEquals(SessionState.Ready(userB), useCase())

            authenticationRepository.currentSession = authenticatedSession(userAId)
            assertEquals(SessionState.Ready(userA), useCase())
        } finally {
            userRepositoryProvider.closeCurrent()
        }
    }

    private fun authenticatedSession(userId: UserId) =
        AuthenticationSessionResult.Authenticated(
            userId = userId,
            isEmailVerified = true,
        )

    private class MutableFakeAuthenticationRepository : AuthenticationRepository {
        var currentSession: AuthenticationSessionResult = AuthenticationSessionResult.NoSession

        override suspend fun registerWithEmail(email: String, password: String): UserId =
            error("No se espera registrar una cuenta en esta prueba")

        override suspend fun signInWithEmail(email: String, password: String): UserId =
            error("No se espera iniciar sesión en esta prueba")

        override suspend fun getCurrentSession(): AuthenticationSessionResult = currentSession

        override suspend fun sendEmailVerification() {
            error("No se espera enviar verificación en esta prueba")
        }
    }
}
