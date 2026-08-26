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
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

class RoomUserRepositoryTest {

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
}
