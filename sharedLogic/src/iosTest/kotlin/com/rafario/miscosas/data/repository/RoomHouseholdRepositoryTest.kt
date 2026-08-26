package com.rafario.miscosas.data.repository

import androidx.room3.executeSQL
import androidx.room3.useWriterConnection
import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.data.local.database.mapper.toEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.Household
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.HouseholdMember
import com.rafario.miscosas.domain.model.HouseholdRole
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

class RoomHouseholdRepositoryTest {

    @Test
    fun createPersistsHouseholdOwnerMembershipAndTwoPendingUpserts() = runTest {
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
            database.userDao().upsert(userEntity)

            val household = Household(
                id = HouseholdId("550e8400-e29b-41d4-a716-446655440001"),
                name = "My Household",
                createdBy = UserId(userEntity.id),
                createdAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
                updatedAt = Instant.parse("2026-08-21T08:00:00.000000800Z"),
            )

            val householdMember = HouseholdMember(
                householdId = household.id,
                userId = household.createdBy,
                role = HouseholdRole.OWNER,
                joinedAt = household.createdAt,
                updatedAt = household.updatedAt,
            )

            val enqueuedAt =
                Instant.parse("2026-08-26T10:15:30.000000789Z")

            var clockCalls = 0
            val fixedClock = object : Clock {
                override fun now(): Instant {
                    clockCalls += 1
                    return enqueuedAt
                }
            }

            val householdMutationId =
                "550e8400-e29b-41d4-a716-446655440091"
            val membershipMutationId =
                "550e8400-e29b-41d4-a716-446655440092"

            val mutationIds = listOf(
                householdMutationId,
                membershipMutationId,
            ).iterator()

            val repository = RoomHouseholdRepository(
                database = database,
                clock = fixedClock,
                generateMutationId = { mutationIds.next() },
            )

            repository.create(
                household = household,
                ownerMembership = householdMember,
            )

            val storedHousehold = database
                .householdDao()
                .findById(household.id.value)

            val storedMembership = database
                .householdMemberDao()
                .findByHouseholdIdAndUserId(
                    householdId = household.id.value,
                    userId = householdMember.userId.value,
                )

            val storedHouseholdSync = database
                .syncOutboxDao()
                .findByTarget(
                    scopeId = household.id.value,
                    recordTypeCode = SyncRecordType.HOUSEHOLD.code,
                    recordId = household.id.value,
                )

            val storedMembershipSync = database
                .syncOutboxDao()
                .findByTarget(
                    scopeId = household.id.value,
                    recordTypeCode = SyncRecordType.HOUSEHOLD_MEMBER.code,
                    recordId = householdMember.userId.value,
                )

            assertEquals(household.toEntity(), storedHousehold)
            assertEquals(householdMember.toEntity(), storedMembership)

            assertEquals(
                SyncOutboxEntity(
                    scopeId = household.id.value,
                    recordTypeCode = SyncRecordType.HOUSEHOLD.code,
                    recordId = household.id.value,
                    mutationId = householdMutationId,
                    operationCode = SyncOperation.UPSERT.code,
                    baseRemoteVersion = null,
                    enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
                    enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
                ),
                storedHouseholdSync,
            )

            assertEquals(
                SyncOutboxEntity(
                    scopeId = household.id.value,
                    recordTypeCode = SyncRecordType.HOUSEHOLD_MEMBER.code,
                    recordId = householdMember.userId.value,
                    mutationId = membershipMutationId,
                    operationCode = SyncOperation.UPSERT.code,
                    baseRemoteVersion = null,
                    enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
                    enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
                ),
                storedMembershipSync,
            )

            assertEquals(1, clockCalls)
        } finally {
            database.close()
        }
    }

    @Test
    fun createRollsBackEverythingWhenSecondOutboxWriteFails() = runTest {
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
            database.userDao().upsert(userEntity)

            val household = Household(
                id = HouseholdId("550e8400-e29b-41d4-a716-446655440001"),
                name = "My Household",
                createdBy = UserId(userEntity.id),
                createdAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
                updatedAt = Instant.parse("2026-08-21T08:00:00.000000800Z"),
            )

            val householdMember = HouseholdMember(
                householdId = household.id,
                userId = household.createdBy,
                role = HouseholdRole.OWNER,
                joinedAt = household.createdAt,
                updatedAt = household.updatedAt,
            )

            database.useWriterConnection { connection ->
                connection.executeSQL(
                    """
                    CREATE TRIGGER fail_second_sync_outbox_insert
                    BEFORE INSERT ON sync_outbox
                    WHEN (SELECT COUNT(*) FROM sync_outbox) >= 1
                    BEGIN
                        SELECT RAISE(ABORT, 'forced second outbox failure');
                    END
                    """.trimIndent(),
                )
            }

            val repository = RoomHouseholdRepository(database)

            assertFailsWith<SQLiteException> {
                repository.create(
                    household = household,
                    ownerMembership = householdMember,
                )
            }

            val findHousehold = database
                .householdDao()
                .findById(household.id.value)

            val findHouseholdMember = database
                .householdMemberDao()
                .findByHouseholdIdAndUserId(
                    householdId = household.id.value,
                    userId = householdMember.userId.value,
                )

            val householdSync = database.syncOutboxDao().findByTarget(
                scopeId = household.id.value,
                recordTypeCode = SyncRecordType.HOUSEHOLD.code,
                recordId = household.id.value,
            )

            val householdMemberSync = database.syncOutboxDao().findByTarget(
                scopeId = household.id.value,
                recordTypeCode = SyncRecordType.HOUSEHOLD_MEMBER.code,
                recordId = householdMember.userId.value,
            )

            assertNull(findHousehold)
            assertNull(findHouseholdMember)
            assertNull(householdSync)
            assertNull(householdMemberSync)
        } finally {
            database.close()
        }
    }
}
