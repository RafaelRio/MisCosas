package com.rafario.miscosas.api.session

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.RemoteUserProfileRepository
import com.rafario.miscosas.domain.repository.UserRepository
import com.rafario.miscosas.domain.repository.UserRepositoryProvider
import com.rafario.miscosas.domain.usecase.GetSessionStateUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Instant

class SessionRestorationFacadeTest {

    @Test
    fun cancelsPreviousRestoreDuringRemoteLookupAndReturnsReadyForNewestUid() = runTest {
        val userA = createUser("user-one-id", "Usuario 1")
        val userB = createUser("user-two-id", "Usuario 2")
        val authenticationRepository = MutableFakeAuthenticationRepository(
            authenticatedSession(userA.id),
        )
        val remoteLookupStartedA = CompletableDeferred<Unit>()
        val allowRemoteLookupA = CompletableDeferred<Unit>()
        val repositoryA = RecordingUserRepository(user = null)
        val repositoryB = RecordingUserRepository(user = userB)
        val userRepositoryProvider = RecordingUserRepositoryProvider(
            repositoriesByUid = mapOf(
                userA.id to repositoryA,
                userB.id to repositoryB,
            ),
        )
        val remoteUserProfileRepository = ControlledRemoteUserProfileRepository(
            user = userA,
            remoteLookupStarted = remoteLookupStartedA,
            allowRemoteLookup = allowRemoteLookupA,
        )
        val facade = createFacade(
            authenticationRepository = authenticationRepository,
            userRepositoryProvider = userRepositoryProvider,
            remoteUserProfileRepository = remoteUserProfileRepository,
        )

        val restoreA = async(start = CoroutineStart.UNDISPATCHED) {
            facade.restoreSession()
        }
        var restoreB: kotlinx.coroutines.Deferred<SessionState>? = null

        try {
            remoteLookupStartedA.await()
            authenticationRepository.session = authenticatedSession(userB.id)

            restoreB = async(start = CoroutineStart.UNDISPATCHED) {
                facade.restoreSession()
            }
            runCurrent()

            assertTrue(restoreA.isCancelled, "La restauración de A debe cancelarse")
            assertFailsWith<CancellationException> {
                restoreA.await()
            }
            assertEquals(SessionState.Ready(userB), restoreB.await())
            assertEquals(listOf(userA.id, userB.id), userRepositoryProvider.requestedUserIds)
            assertEquals(listOf(userA.id), remoteUserProfileRepository.requestedUserIds)
            assertEquals(emptyList(), repositoryA.appliedSnapshots)
        } finally {
            restoreA.cancelAndJoin()
            restoreB?.cancelAndJoin()
        }
    }

    @Test
    fun cancelledRestoreDoesNotApplyRemoteSnapshotWhenCancellationOccursBeforeWrite() = runTest {
        val userA = createUser("user-one-id", "Usuario 1")
        val userB = createUser("user-two-id", "Usuario 2")
        val authenticationRepository = MutableFakeAuthenticationRepository(
            authenticatedSession(userA.id),
        )
        val remoteLookupStartedA = CompletableDeferred<Unit>()
        val allowRemoteLookupA = CompletableDeferred<Unit>()
        val snapshotApplicationStartedA = CompletableDeferred<Unit>()
        val allowSnapshotApplicationA = CompletableDeferred<Unit>()
        val repositoryA = RecordingUserRepository(
            user = null,
            beforeApplyingSnapshot = {
                snapshotApplicationStartedA.complete(Unit)
                allowSnapshotApplicationA.await()
            },
        )
        val repositoryB = RecordingUserRepository(user = userB)
        val userRepositoryProvider = RecordingUserRepositoryProvider(
            repositoriesByUid = mapOf(
                userA.id to repositoryA,
                userB.id to repositoryB,
            ),
        )
        val remoteUserProfileRepository = ControlledRemoteUserProfileRepository(
            user = userA,
            remoteLookupStarted = remoteLookupStartedA,
            allowRemoteLookup = allowRemoteLookupA,
        )
        val facade = createFacade(
            authenticationRepository = authenticationRepository,
            userRepositoryProvider = userRepositoryProvider,
            remoteUserProfileRepository = remoteUserProfileRepository,
        )

        val restoreA = async(start = CoroutineStart.UNDISPATCHED) {
            facade.restoreSession()
        }
        var restoreB: kotlinx.coroutines.Deferred<SessionState>? = null

        try {
            remoteLookupStartedA.await()
            allowRemoteLookupA.complete(Unit)
            snapshotApplicationStartedA.await()
            authenticationRepository.session = authenticatedSession(userB.id)

            restoreB = async(start = CoroutineStart.UNDISPATCHED) {
                facade.restoreSession()
            }
            runCurrent()

            assertTrue(restoreA.isCancelled, "La restauración de A debe cancelarse")
            assertFailsWith<CancellationException> {
                restoreA.await()
            }
            assertEquals(SessionState.Ready(userB), restoreB.await())
            assertEquals(emptyList(), repositoryA.appliedSnapshots)
            assertEquals(listOf(userA.id, userB.id), userRepositoryProvider.requestedUserIds)
        } finally {
            restoreA.cancelAndJoin()
            restoreB?.cancelAndJoin()
        }
    }

    private fun createFacade(
        authenticationRepository: AuthenticationRepository,
        userRepositoryProvider: UserRepositoryProvider,
        remoteUserProfileRepository: RemoteUserProfileRepository,
    ): SessionRestorationFacade = SessionRestorationFacade(
        GetSessionStateUseCase(
            repository = authenticationRepository,
            userRepositoryProvider = userRepositoryProvider,
            remoteUserProfileRepository = remoteUserProfileRepository,
        ),
    )

    private fun authenticatedSession(userId: UserId) =
        AuthenticationSessionResult.Authenticated(
            userId = userId,
            isEmailVerified = true,
        )

    private fun createUser(id: String, displayName: String) = User(
        id = UserId(id),
        displayName = displayName,
        createdAt = Instant.parse("2026-08-27T09:00:00.000000400Z"),
        updatedAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
    )

    private class MutableFakeAuthenticationRepository(
        var session: AuthenticationSessionResult,
    ) : AuthenticationRepository {
        override suspend fun registerWithEmail(email: String, password: String): UserId =
            error("No se espera registrar una cuenta durante la restauración")

        override suspend fun signInWithEmail(email: String, password: String): UserId =
            error("No se espera iniciar sesión durante la restauración")

        override suspend fun getCurrentSession(): AuthenticationSessionResult = session

        override suspend fun sendEmailVerification() {
            error("No se espera solicitar verificación durante la restauración")
        }
    }

    private class ControlledRemoteUserProfileRepository(
        private val user: User,
        private val remoteLookupStarted: CompletableDeferred<Unit>,
        private val allowRemoteLookup: CompletableDeferred<Unit>,
    ) : RemoteUserProfileRepository {
        val requestedUserIds = mutableListOf<UserId>()

        override suspend fun findById(userId: UserId): User? {
            requestedUserIds += userId
            check(userId == user.id) {
                "No se esperaba consultar el perfil remoto de ${userId.value}"
            }
            remoteLookupStarted.complete(Unit)
            allowRemoteLookup.await()
            return user
        }

        override suspend fun upsert(user: User) {
            error("No se espera escribir en el repositorio remoto")
        }
    }

    private class RecordingUserRepositoryProvider(
        private val repositoriesByUid: Map<UserId, UserRepository>,
    ) : UserRepositoryProvider {
        val requestedUserIds = mutableListOf<UserId>()

        override fun forUser(userId: UserId): UserRepository {
            requestedUserIds += userId
            return checkNotNull(repositoriesByUid[userId]) {
                "No existe un repositorio local para ${userId.value}"
            }
        }

        override fun closeCurrent() = Unit
    }

    private class RecordingUserRepository(
        private val user: User?,
        private val beforeApplyingSnapshot: suspend () -> Unit = {},
    ) : UserRepository {
        val appliedSnapshots = mutableListOf<User>()

        override suspend fun findById(userId: UserId): User? =
            user?.takeIf { it.id == userId }

        override suspend fun save(user: User) {
            error("No se espera guardar un perfil local durante la restauración")
        }

        override suspend fun applyRemoteSnapshot(user: User) {
            beforeApplyingSnapshot()
            appliedSnapshots += user
        }
    }
}
