package com.rafario.miscosas.data.local.database.dao

import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SyncOutboxDaoTest {

    @Test
    fun upsertsAndFindsOperationByTarget() = runTest {
        val database = createTestDatabase()
        try {
            val entity = SyncOutboxEntity(
                scopeId = "scope_id",
                recordTypeCode = SyncRecordType.ITEM.code,
                recordId = "record_id",
                mutationId = "mutation_id",
                operationCode = SyncOperation.UPSERT.code,
                baseRemoteVersion = null,
                enqueuedAtEpochSeconds = 456,
                enqueuedAtNanoseconds = 789,
            )
            database.syncOutboxDao().upsert(entity)

            val storedEntity = database.syncOutboxDao().findByTarget(
                scopeId = entity.scopeId,
                recordTypeCode = entity.recordTypeCode,
                recordId = entity.recordId,
            )

            assertEquals(entity, storedEntity)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertReplacesPendingOperationForSameTarget() = runTest {
        val database = createTestDatabase()
        try {
            val original = SyncOutboxEntity(
                scopeId = "scope_id",
                recordTypeCode = SyncRecordType.ITEM.code,
                recordId = "record_id",
                mutationId = "mutation_id",
                operationCode = SyncOperation.UPSERT.code,
                baseRemoteVersion = null,
                enqueuedAtEpochSeconds = 456,
                enqueuedAtNanoseconds = 789,
            )
            val replacement = original.copy(
                mutationId = "replacement_mutation_id",
                operationCode = SyncOperation.DELETE.code,
                enqueuedAtNanoseconds = 900,
                enqueuedAtEpochSeconds = 550,
            )
            database.syncOutboxDao().upsert(original)
            database.syncOutboxDao().upsert(replacement)

            val storedEntity = database.syncOutboxDao().findByTarget(
                scopeId = replacement.scopeId,
                recordTypeCode = replacement.recordTypeCode,
                recordId = replacement.recordId,
            )

            assertEquals(replacement, storedEntity)
        } finally {
            database.close()
        }
    }

    @Test
    fun findsPendingOperationsInEnqueueOrder() = runTest {
        val database = createTestDatabase()
        try {
            val original = SyncOutboxEntity(
                scopeId = "scope_id",
                recordTypeCode = SyncRecordType.ITEM.code,
                recordId = "record_id",
                mutationId = "mutation_id",
                operationCode = SyncOperation.UPSERT.code,
                baseRemoteVersion = null,
                enqueuedAtEpochSeconds = 456,
                enqueuedAtNanoseconds = 789,
            )

            val latest = original.copy(
                recordId = "latest_record_id",
                mutationId = "latest_mutation_id",
                enqueuedAtNanoseconds = 0,
                enqueuedAtEpochSeconds = 2000,
            )

            val middle = original.copy(
                recordId = "middle_record_id",
                mutationId = "middle_mutation_id",
                enqueuedAtNanoseconds = 900,
                enqueuedAtEpochSeconds = 1000,
            )

            val earliest = original.copy(
                recordId = "earliest_record_id",
                mutationId = "earliest_mutation_id",
                enqueuedAtNanoseconds = 100,
                enqueuedAtEpochSeconds = 1000,
            )

            database.syncOutboxDao().upsert(latest)
            database.syncOutboxDao().upsert(earliest)
            database.syncOutboxDao().upsert(middle)

            val pendingOperations = database.syncOutboxDao().findAllPending()

            assertEquals(
                expected = listOf(earliest, middle, latest),
                actual = pendingOperations,
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun deletesOnlyWhenMutationIdMatchesCurrentOperation() = runTest {
        val database = createTestDatabase()
        try {
            val original = SyncOutboxEntity(
                scopeId = "scope_id",
                recordTypeCode = SyncRecordType.ITEM.code,
                recordId = "record_id",
                mutationId = "mutation_id",
                operationCode = SyncOperation.UPSERT.code,
                baseRemoteVersion = null,
                enqueuedAtEpochSeconds = 456,
                enqueuedAtNanoseconds = 789,
            )
            val replacement = original.copy(
                mutationId = "replacement_mutation_id",
                operationCode = SyncOperation.DELETE.code,
                enqueuedAtNanoseconds = 900,
                enqueuedAtEpochSeconds = 1000,
            )
            database.syncOutboxDao().upsert(original)
            database.syncOutboxDao().upsert(replacement)

            val deletedByStaleAcknowledgement = database.syncOutboxDao().deleteIfMutationMatches(
                scopeId = original.scopeId,
                recordTypeCode = original.recordTypeCode,
                recordId = original.recordId,
                mutationId = original.mutationId,
            )

            val storedAfterStaleAcknowledgement = database.syncOutboxDao().findByTarget(
                scopeId = replacement.scopeId,
                recordTypeCode = replacement.recordTypeCode,
                recordId = replacement.recordId,
            )

            assertEquals(0, deletedByStaleAcknowledgement)
            assertEquals(replacement, storedAfterStaleAcknowledgement)

            val deletedByCurrentAcknowledgement = database.syncOutboxDao().deleteIfMutationMatches(
                scopeId = replacement.scopeId,
                recordTypeCode = replacement.recordTypeCode,
                recordId = replacement.recordId,
                mutationId = replacement.mutationId,
            )
            assertEquals(1, deletedByCurrentAcknowledgement)

            val storedAfterCurrentAcknowledgement = database.syncOutboxDao().findByTarget(
                scopeId = replacement.scopeId,
                recordTypeCode = replacement.recordTypeCode,
                recordId = replacement.recordId,
            )
            assertNull(storedAfterCurrentAcknowledgement)
        } finally {
            database.close()
        }
    }
}
