package com.rafario.miscosas.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rafario.miscosas.data.local.database.createAndroidUserDatabaseProvider
import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.RemoteUserProfileRepository
import com.rafario.miscosas.domain.repository.UserRepository
import com.rafario.miscosas.domain.usecase.GetSessionStateUseCase
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

@RunWith(AndroidJUnit4::class)
class RoomUserRepositoryProviderAndroidTest {

    @Test
    fun sessionStateUsesAndroidRoomPartitionForEachAuthenticatedUid() = runTest {
        val testRunId = Uuid.random().toString()
        val userAId = UserId("auth-t16-android-$testRunId-user-a")
        val userBId = UserId("auth-t16-android-$testRunId-user-b")
        val authenticationRepository = MutableFakeAuthenticationRepository()
        val userRepositoryProvider = RoomUserRepositoryProvider(
            database = createAndroidUserDatabaseProvider(
                InstrumentationRegistry.getInstrumentation().targetContext,
            ),
            repository = authenticationRepository,
        )
        val remoteUserProfileRepository = NoProfileRemoteUserProfileRepository()
        val useCase = GetSessionStateUseCase(
            repository = authenticationRepository,
            userRepositoryProvider = userRepositoryProvider,
            remoteUserProfileRepository = remoteUserProfileRepository,
        )
        val userA = user(userAId, "Perfil Android A")
        val userB = user(userBId, "Perfil Android B")

        try {
            authenticationRepository.currentSession = authenticatedSession(userAId)
            userRepositoryProvider.forUser(userAId).save(userA)

            authenticationRepository.currentSession = authenticatedSession(userBId)
            userRepositoryProvider.forUser(userBId).save(userB)

            authenticationRepository.currentSession = authenticatedSession(userAId)
            assertEquals(SessionState.Ready(userA), useCase())

            authenticationRepository.currentSession = authenticatedSession(userBId)
            assertEquals(SessionState.Ready(userB), useCase())

            authenticationRepository.currentSession = authenticatedSession(userAId)
            assertEquals(SessionState.Ready(userA), useCase())

            assertEquals(emptyList(), remoteUserProfileRepository.requestedUserIds)
        } finally {
            userRepositoryProvider.closeCurrent()
        }
    }

    private fun user(userId: UserId, displayName: String) = User(
        id = userId,
        displayName = displayName,
        createdAt = Instant.parse("2026-08-27T09:00:00.000000400Z"),
        updatedAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
    )

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

    private class NoProfileRemoteUserProfileRepository : RemoteUserProfileRepository {
        val requestedUserIds = mutableListOf<UserId>()

        override suspend fun findById(userId: UserId): User? {
            requestedUserIds += userId
            return null
        }

        override suspend fun upsert(user: User) {
            error("No se espera escribir en el repositorio remoto")
        }
    }
}
