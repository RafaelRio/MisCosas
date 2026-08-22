package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.HouseholdMemberEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class HouseholdMemberDaoTest {

    @Test
    fun upsertsAndFindsMemberByCompositeId() = runTest {
        val database = createTestDatabase()

        try {
            val creator = UserEntity(
                id = "firebase-user_A1b2C3",
                displayName = "Rafael",
                createdAtEpochSeconds = 1_776_000_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_003_600L,
                updatedAtNanoseconds = 500,
            )
            val household = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                name = "Casa Río",
                createdBy = creator.id,
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )
            val householdMember = HouseholdMemberEntity(
                householdId = household.id,
                userId = creator.id,
                roleCode = "owner",
                joinedAtEpochSeconds = 1_776_003_600L,
                joinedAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 800,
            )

            database.userDao().upsert(creator)
            database.householdDao().upsert(household)
            database.householdMemberDao().upsert(householdMember)

            val storedMember = database.householdMemberDao()
                .findByHouseholdIdAndUserId(
                    householdMember.householdId,
                    householdMember.userId,
                )

            assertEquals(householdMember, storedMember)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingMember() = runTest {
        val database = createTestDatabase()

        try {
            val creator = UserEntity(
                id = "firebase-user_A1b2C3",
                displayName = "Rafael",
                createdAtEpochSeconds = 1_776_000_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_003_600L,
                updatedAtNanoseconds = 500,
            )
            val household = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                name = "Casa Río",
                createdBy = creator.id,
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )
            val householdMember = HouseholdMemberEntity(
                householdId = household.id,
                userId = creator.id,
                roleCode = "member",
                joinedAtEpochSeconds = 1_776_003_600L,
                joinedAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 800,
            )
            val updatedMember = householdMember.copy(
                roleCode = "owner",
                updatedAtEpochSeconds = 1_776_010_800L,
                updatedAtNanoseconds = 900,
            )

            database.userDao().upsert(creator)
            database.householdDao().upsert(household)
            database.householdMemberDao().upsert(householdMember)
            database.householdMemberDao().upsert(updatedMember)

            val storedMember = database.householdMemberDao()
                .findByHouseholdIdAndUserId(
                    updatedMember.householdId,
                    updatedMember.userId,
                )

            assertEquals(updatedMember, storedMember)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenMembershipDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedMember = database.householdMemberDao()
                .findByHouseholdIdAndUserId(
                    "missing-household",
                    "missing-user",
                )

            assertNull(storedMember)
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsMembershipWhenHouseholdDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val creator = UserEntity(
                id = "firebase-user_A1b2C3",
                displayName = "Rafael",
                createdAtEpochSeconds = 1_776_000_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_003_600L,
                updatedAtNanoseconds = 500,
            )
            val householdMember = HouseholdMemberEntity(
                householdId = "missing-household",
                userId = creator.id,
                roleCode = "member",
                joinedAtEpochSeconds = 1_776_003_600L,
                joinedAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 800,
            )

            database.userDao().upsert(creator)

            assertFailsWith<SQLiteException> {
                database.householdMemberDao().upsert(householdMember)
            }

            assertNull(
                database.householdMemberDao()
                    .findByHouseholdIdAndUserId(
                        householdMember.householdId,
                        householdMember.userId,
                    ),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsMembershipWhenUserDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val creator = UserEntity(
                id = "firebase-user_A1b2C3",
                displayName = "Rafael",
                createdAtEpochSeconds = 1_776_000_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_003_600L,
                updatedAtNanoseconds = 500,
            )
            val household = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                name = "Casa Río",
                createdBy = creator.id,
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )
            val householdMember = HouseholdMemberEntity(
                householdId = household.id,
                userId = "missing-user",
                roleCode = "member",
                joinedAtEpochSeconds = 1_776_003_600L,
                joinedAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 800,
            )

            database.userDao().upsert(creator)
            database.householdDao().upsert(household)

            assertFailsWith<SQLiteException> {
                database.householdMemberDao().upsert(householdMember)
            }
            val storedMember = database.householdMemberDao()
                .findByHouseholdIdAndUserId(
                    householdMember.householdId,
                    householdMember.userId,
                )

            assertNull(storedMember)

        } finally {
            database.close()
        }
    }
}
