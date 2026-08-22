package com.rafario.miscosas.data.local.database.dao

import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserDaoTest {

    @Test
    fun upsertsAndFindsUserById() = runTest {
        val database = createTestDatabase()

        try {
            val entity = UserEntity(
                id = "firebase-user_A1b2C3",
                displayName = "Rafael",
                createdAtEpochSeconds = 1_776_000_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_003_600L,
                updatedAtNanoseconds = 500,
            )

            database.userDao().upsert(entity)

            val storedEntity = database.userDao().findById(entity.id)

            assertEquals(entity, storedEntity)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingUser() = runTest {
        val database = createTestDatabase()

        try {
            val original = UserEntity(
                id = "firebase-user_A1b2C3",
                displayName = "Rafael",
                createdAtEpochSeconds = 1_776_000_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_003_600L,
                updatedAtNanoseconds = 500,
            )
            val updated = original.copy(
                displayName = "Rafael actualizado",
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 600,
            )

            database.userDao().upsert(original)
            database.userDao().upsert(updated)

            val storedEntity = database.userDao().findById(original.id)

            assertEquals(updated, storedEntity)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenUserDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedEntity = database.userDao()
                .findById("missing-user")

            assertNull(storedEntity)
        } finally {
            database.close()
        }
    }
}