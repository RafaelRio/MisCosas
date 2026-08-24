package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.ReturnPeriodEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ReturnPeriodDaoTest {

    @Test
    fun upsertsAndFindsReturnPeriodByItemId() = runTest {
        val database = createTestDatabase()

        try {
            val item = insertItemParent(database)
            val returnPeriod = createReturnPeriodEntity(itemId = item.id)

            database.returnPeriodDao().upsert(returnPeriod)

            val storedReturnPeriod = database.returnPeriodDao()
                .findByItemId(item.id)

            assertEquals(returnPeriod, storedReturnPeriod)
        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingReturnPeriod() = runTest {
        val database = createTestDatabase()

        try {
            val item = insertItemParent(database)
            val returnPeriod = createReturnPeriodEntity(itemId = item.id)
            val updatedPeriod = returnPeriod.copy(
                seller = "Proveedor actualizado",
                updatedAtNanoseconds = 900,
                trackingStateCode = "returned",
            )

            database.returnPeriodDao().upsert(returnPeriod)
            database.returnPeriodDao().upsert(updatedPeriod)

            val storedReturnPeriod = database.returnPeriodDao()
                .findByItemId(item.id)

            assertEquals(updatedPeriod, storedReturnPeriod)
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenReturnPeriodDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedReturnPeriod = database.returnPeriodDao()
                .findByItemId("missing-item")

            assertNull(storedReturnPeriod)
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsReturnPeriodWhenItemDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val returnPeriod = createReturnPeriodEntity(itemId = "missing-item")

            assertFailsWith<SQLiteException> {
                database.returnPeriodDao().upsert(returnPeriod)
            }

            assertNull(database.returnPeriodDao().findByItemId(returnPeriod.itemId))
        } finally {
            database.close()
        }
    }

    private suspend fun insertItemParent(database: MisCosasDatabase): ItemEntity {
        val user = UserEntity(
            id = "firebase-user_A1b2C3",
            displayName = "Rafael",
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )
        val household = HouseholdEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            name = "Casa Río",
            createdBy = user.id,
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )
        val item = ItemEntity(
            id = "550e8200-e29b-41d4-a716-446655440010",
            householdId = household.id,
            name = "Televisor",
            categoryId = "builtin:image-and-sound",
            brand = "LG",
            model = "OLED",
            serialNumber = "TV-123",
            purchaseDateEpochDay = null,
            purchasePriceMinorUnits = null,
            purchaseCurrencyCode = null,
            purchaseSeller = null,
            statusCode = "active",
            isFavorite = false,
            isArchived = false,
            createdAtEpochSeconds = 1_000L,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = 1_000L,
            updatedAtNanoseconds = 500,
        )

        database.userDao().upsert(user)
        database.householdDao().upsert(household)
        database.itemDao().upsert(item)

        return item
    }

    private fun createReturnPeriodEntity(
        itemId: String,
        seller: String? = "Proveedor",
        deadlineEpochDay: Long = 20_365L,
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 400,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 500,
        trackingStateCode: String = "tracking",
    ): ReturnPeriodEntity = ReturnPeriodEntity(
        itemId = itemId,
        seller = seller,
        deadlineEpochDay = deadlineEpochDay,
        createdAtEpochSeconds = createdAtEpochSeconds,
        createdAtNanoseconds = createdAtNanoseconds,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
        updatedAtNanoseconds = updatedAtNanoseconds,
        trackingStateCode = trackingStateCode,
    )
}
