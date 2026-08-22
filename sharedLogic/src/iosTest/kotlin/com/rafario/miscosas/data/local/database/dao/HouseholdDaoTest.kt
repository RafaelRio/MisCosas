package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class HouseholdDaoTest {

    @Test
    fun upsertsAndFindsHouseholdById() = runTest {
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

            database.userDao().upsert(creator)
            database.householdDao().upsert(household)

            val storedHousehold = database.householdDao()
                .findById(household.id)

            assertEquals(household, storedHousehold)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingHousehold() = runTest {
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
            val original = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                name = "Casa Río",
                createdBy = creator.id,
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )
            val updated = original.copy(
                name = "Casa familiar",
                updatedAtEpochSeconds = 1_776_010_800L,
                updatedAtNanoseconds = 800,
            )

            database.userDao().upsert(creator)
            database.householdDao().upsert(original)
            database.householdDao().upsert(updated)

            val storedHousehold = database.householdDao()
                .findById(original.id)

            assertEquals(updated, storedHousehold)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenHouseholdDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedHousehold = database.householdDao()
                .findById("550e8400-e29b-41d4-a716-446655440000")

            assertNull(storedHousehold)
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsHouseholdWhenCreatorDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val household = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                name = "Casa Río",
                createdBy = "missing-user",
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )

            assertFailsWith<SQLiteException> {
                database.householdDao().upsert(household)
            }

            assertNull(
                database.householdDao().findById(household.id),
            )
        } finally {
            database.close()
        }
    }
}