package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ItemDaoTest {

    @Test
    fun upsertsAndFindsItemById() = runTest {
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
                id = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                name = "Casa Río",
                createdBy = creator.id,
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )

            val item = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                householdId = household.id,
                name = "Producto",
                categoryId = "custom:photography",
                brand = "Marca",
                model = "Modelo",
                serialNumber = "Número de serie",
                purchaseDateEpochDay = 1_000,
                purchasePriceMinorUnits = 1_000,
                purchaseCurrencyCode = "EUR",
                purchaseSeller = "Tienda",
                statusCode = "sold",
                isFavorite = true,
                isArchived = true,
                createdAtEpochSeconds = 1_776_00,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_00,
                updatedAtNanoseconds = 500,
            )

            database.userDao().upsert(creator)
            database.householdDao().upsert(household)
            database.itemDao().upsert(item)

            val storedItem = database.itemDao().findById(item.id)

            assertEquals(item, storedItem)

        } finally {
            database.close()
        }
    }

    @Test
    fun upsertUpdatesExistingItem() = runTest {
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
                id = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                name = "Casa Río",
                createdBy = creator.id,
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )

            val item = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                householdId = household.id,
                name = "Producto",
                categoryId = "custom:photography",
                brand = "Marca",
                model = "Modelo",
                serialNumber = "Número de serie",
                purchaseDateEpochDay = 1_000,
                purchasePriceMinorUnits = 1_000,
                purchaseCurrencyCode = "EUR",
                purchaseSeller = "Tienda",
                statusCode = "sold",
                isFavorite = true,
                isArchived = true,
                createdAtEpochSeconds = 1_776_00,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_00,
                updatedAtNanoseconds = 500,
            )
            val updatedItem = item.copy(
                name = "Producto actualizado",
                isFavorite = false,
                updatedAtEpochSeconds = 1_794_00,
            )

            database.userDao().upsert(creator)
            database.householdDao().upsert(household)
            database.itemDao().upsert(item)
            database.itemDao().upsert(updatedItem)
            val storedItem = database.itemDao().findById(item.id)

            assertEquals(updatedItem, storedItem)

        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenItemDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val storedItem = database.itemDao().findById("550e8400-e29b-41d4-a716-446655440000")

            assertNull(storedItem)
        } finally {
            database.close()
        }
    }

    @Test
    fun observesItemsByHouseholdIdAndEmitsAfterChanges() = runTest {
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
                id = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                name = "Casa Río",
                createdBy = creator.id,
                createdAtEpochSeconds = 1_776_003_600L,
                createdAtNanoseconds = 600,
                updatedAtEpochSeconds = 1_776_007_200L,
                updatedAtNanoseconds = 700,
            )
            val item = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                householdId = household.id,
                name = "Producto",
                categoryId = "custom:photography",
                brand = "Marca",
                model = "Modelo",
                serialNumber = "Número de serie",
                purchaseDateEpochDay = 1_000,
                purchasePriceMinorUnits = 1_000,
                purchaseCurrencyCode = "EUR",
                purchaseSeller = "Tienda",
                statusCode = "active",
                isFavorite = false,
                isArchived = false,
                createdAtEpochSeconds = 1_776_000_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_000_000L,
                updatedAtNanoseconds = 500,
            )

            database.userDao().upsert(creator)
            database.householdDao().upsert(household)

            val initialEmissionReceived = CompletableDeferred<Unit>()
            val emissions = backgroundScope.async {
                database.itemDao()
                    .observeByHouseholdId(household.id)
                    .take(2)
                    .onEach { initialEmissionReceived.complete(Unit) }
                    .toList()
            }

            initialEmissionReceived.await()
            database.itemDao().upsert(item)

            assertEquals(
                expected = listOf(emptyList(), listOf(item)),
                actual = emissions.await(),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsItemWhenHouseholdDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val item = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                householdId = "missing-household",
                name = "Producto",
                categoryId = "custom:photography",
                brand = "Marca",
                model = "Modelo",
                serialNumber = "Número de serie",
                purchaseDateEpochDay = 1_000,
                purchasePriceMinorUnits = 1_000,
                purchaseCurrencyCode = "EUR",
                purchaseSeller = "Tienda",
                statusCode = "sold",
                isFavorite = true,
                isArchived = true,
                createdAtEpochSeconds = 1_776_00,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_776_00,
                updatedAtNanoseconds = 500,
            )

            assertFailsWith<SQLiteException> {
                database.itemDao().upsert(item)
            }
            assertNull(database.itemDao().findById(item.id))

        } finally {
            database.close()
        }
    }
}
