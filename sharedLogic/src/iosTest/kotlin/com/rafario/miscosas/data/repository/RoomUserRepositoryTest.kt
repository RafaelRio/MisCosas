package com.rafario.miscosas.data.repository

import androidx.room3.executeSQL
import androidx.room3.useWriterConnection
import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.data.local.database.mapper.toDomain
import com.rafario.miscosas.data.local.database.mapper.toEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.session.OwnProfileAccessGuard
import com.rafario.miscosas.domain.session.ProfileAccessDeniedException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

class RoomUserRepositoryTest {

    @Test
    fun findByIdReturnsNullWhenUserDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val rafa = User(
                id = UserId("firebase-user-164"),
                displayName = "Rafael Río",
                createdAt =
                    Instant.parse("2026-08-27T09:00:00.000000400Z"),
                updatedAt =
                    Instant.parse("2026-08-27T09:00:00.000000500Z"),
            )
            database.userDao().upsert(rafa.toEntity())
            val repository = RoomUserRepository(
                database = database,
                ownProfileAccessGuard = ownProfileAccessGuard(
                    userId = UserId("firebase-user-123"),
                ),
            )

            val user = repository.findById(UserId("firebase-user-123"))

            assertNull(user)
        } finally {
            database.close()
        }
    }

    @Test
    fun savePersistsUserAndPendingUpsert() = runTest {
        val database = createTestDatabase()

        try {
            val enqueuedAt =
                Instant.parse("2026-08-27T09:00:01.000000789Z")

            val fixedClock = object : Clock {
                override fun now(): Instant = enqueuedAt
            }

            val mutationId =
                "550e8400-e29b-41d4-a716-446655440099"

            val user = User(
                id = UserId("firebase-user-123"),
                displayName = "Rafael Río",
                createdAt =
                    Instant.parse("2026-08-27T09:00:00.000000400Z"),
                updatedAt =
                    Instant.parse("2026-08-27T09:00:00.000000500Z"),
            )

            val repository = RoomUserRepository(
                database = database,
                ownProfileAccessGuard = ownProfileAccessGuard(user.id),
                clock = fixedClock,
                generateMutationId = { mutationId },
            )

            repository.save(user)

            val storedUser = database.userDao().findById(user.id.value)

            val storedSyncOperation = database.syncOutboxDao().findByTarget(
                scopeId = user.id.value,
                recordTypeCode = SyncRecordType.USER.code,
                recordId = user.id.value,
            )

            assertEquals(user.toEntity(), storedUser)

            assertEquals(
                SyncOutboxEntity(
                    scopeId = user.id.value,
                    recordTypeCode = SyncRecordType.USER.code,
                    recordId = user.id.value,
                    mutationId = mutationId,
                    operationCode = SyncOperation.UPSERT.code,
                    baseRemoteVersion = null,
                    enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
                    enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
                ),
                storedSyncOperation,
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun unverifiedSessionCanSaveOnlyItsOwnProfile() = runTest {
        val database = createTestDatabase()
        try {
            val authenticatedUserId = UserId("firebase-user-123")
            val ownProfile = User(
                id = authenticatedUserId,
                displayName = "Rafael Río",
                createdAt = Instant.parse("2026-08-27T09:00:00.000000400Z"),
                updatedAt = Instant.parse("2026-08-27T09:00:00.000000500Z"),
            )
            val anotherProfile = ownProfile.copy(
                id = UserId("firebase-user-456"),
                displayName = "Another User",
            )
            val repository = RoomUserRepository(
                database = database,
                ownProfileAccessGuard = ownProfileAccessGuard(
                    userId = authenticatedUserId,
                    isEmailVerified = false,
                ),
            )

            repository.save(ownProfile)

            assertEquals(
                ownProfile.toEntity(),
                database.userDao().findById(authenticatedUserId.value),
            )
            assertFailsWith<ProfileAccessDeniedException> {
                repository.save(anotherProfile)
            }
            assertNull(database.userDao().findById(anotherProfile.id.value))
            assertNull(
                database.syncOutboxDao().findByTarget(
                    scopeId = anotherProfile.id.value,
                    recordTypeCode = SyncRecordType.USER.code,
                    recordId = anotherProfile.id.value,
                ),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun saveRollsBackUserWhenOutboxWriteFails() = runTest {
        val database = createTestDatabase()
        try {
            val userEntity = UserEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                displayName = "John Doe",
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )
            val user = userEntity.toDomain()

            val repository = RoomUserRepository(
                database = database,
                ownProfileAccessGuard = ownProfileAccessGuard(
                    userId = UserId(userEntity.id),
                ),
            )
            database.useWriterConnection { connection ->
                connection.executeSQL(
                    """
                    CREATE TRIGGER fail_sync_outbox_insert
                    BEFORE INSERT ON sync_outbox
                    BEGIN
                        SELECT RAISE(ABORT, 'forced outbox failure');
                    END
                    """.trimIndent(),
                )
            }

            assertFailsWith<SQLiteException> {
                repository.save(user)
            }

            assertNull(
                database.userDao().findById(user.id.value),
            )

            assertNull(
                database.syncOutboxDao().findByTarget(
                    scopeId = user.id.value,
                    recordTypeCode = SyncRecordType.USER.code,
                    recordId = user.id.value,
                ),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun findByIdReturnsExistingUser() = runTest {
        val database = createTestDatabase()
        try {
            val anto = User(
                id = UserId("firebase-user-124"),
                displayName = "Anto Río",
                createdAt =
                    Instant.parse("2026-08-28T09:00:00.000000400Z"),
                updatedAt =
                    Instant.parse("2026-08-29T09:00:00.000000500Z"),
            )
            val user = User(
                id = UserId("firebase-user-123"),
                displayName = "Rafael Río",
                createdAt =
                    Instant.parse("2026-08-27T09:00:00.000000400Z"),
                updatedAt =
                    Instant.parse("2026-08-27T09:00:00.000000500Z"),
            )
            database.userDao().upsert(anto.toEntity())
            database.userDao().upsert(user.toEntity())

            val repository = RoomUserRepository(
                database = database,
                ownProfileAccessGuard = ownProfileAccessGuard(user.id),
            )

            val savedUser = repository.findById(user.id)

            assertEquals(user, savedUser)

        } finally {
            database.close()
        }
    }

    private fun ownProfileAccessGuard(
        userId: UserId,
        isEmailVerified: Boolean = true,
    ): OwnProfileAccessGuard = OwnProfileAccessGuard(
        authenticationRepository = FakeAuthenticationRepository(
            result = AuthenticationSessionResult.Authenticated(
                userId = userId,
                isEmailVerified = isEmailVerified,
            ),
        ),
    )

    private class FakeAuthenticationRepository(
        private val result: AuthenticationSessionResult,
    ) : AuthenticationRepository {
        override suspend fun registerWithEmail(email: String, password: String): UserId =
            error("Registration is not expected in this repository test")

        override suspend fun signInWithEmail(email: String, password: String): UserId =
            error("Sign-in is not expected in this repository test")

        override suspend fun getCurrentSession(): AuthenticationSessionResult = result
    }
}
