package com.rafario.miscosas.data.repository

import androidx.room3.executeSQL
import androidx.room3.useWriterConnection
import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.data.local.database.mapper.toDomain
import com.rafario.miscosas.data.local.database.mapper.toEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.ItemId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

class RoomItemRepositoryTest {

    @Test
    fun findByIdReturnsMappedItem() = runTest {
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

            val householdEntity = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440001",
                name = "My Household",
                createdBy = userEntity.id,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            val itemEntity = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440002",
                householdId = householdEntity.id,
                name = "Item 1",
                categoryId = "550e8400-e29b-41d4-a716-446655440003",
                brand = "Brand 1",
                model = "Model 1",
                serialNumber = "Serial 1",
                purchaseDateEpochDay = 1_000L,
                purchasePriceMinorUnits = 1_000L,
                purchaseCurrencyCode = "USD",
                purchaseSeller = "Seller 1",
                statusCode = "active",
                isFavorite = true,
                isArchived = false,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            database.userDao().upsert(userEntity)
            database.householdDao().upsert(householdEntity)
            database.itemDao().upsert(itemEntity)

            val repository = RoomItemRepository(database)
            val result = repository.findById(ItemId(itemEntity.id))
            val expected = itemEntity.toDomain()
            assertEquals(expected, result)
        } finally {
            database.close()
        }
    }

    @Test
    fun observeByHouseholdIdReturnsOnlyMappedItemsForRequestedHousehold() = runTest {
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

            val houseOne = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440001",
                name = "My Household",
                createdBy = userEntity.id,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            val targetHousehold = houseOne.copy(
                id = "550e8400-e29b-41d4-a716-446655440002",
                name = "My Household 2",
            )

            val itemHomeOne = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440005",
                householdId = houseOne.id,
                name = "Item 1",
                categoryId = "550e8400-e29b-41d4-a716-446655440003",
                brand = "Brand 1",
                model = "Model 1",
                serialNumber = "Serial 1",
                purchaseDateEpochDay = 1_000L,
                purchasePriceMinorUnits = 1_000L,
                purchaseCurrencyCode = "USD",
                purchaseSeller = "Seller 1",
                statusCode = "active",
                isFavorite = true,
                isArchived = false,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            val targetItem = itemHomeOne.copy(
                id = "550e8400-e29b-41d4-a716-446655440006",
                householdId = targetHousehold.id,
            )

            database.userDao().upsert(userEntity)
            database.householdDao().upsert(houseOne)
            database.householdDao().upsert(targetHousehold)
            database.itemDao().upsert(itemHomeOne)
            database.itemDao().upsert(targetItem)

            val repository = RoomItemRepository(database)
            val result = repository.observeByHouseholdId(HouseholdId(targetHousehold.id)).first()

            assertEquals(listOf(targetItem.toDomain()), result)
        } finally {
            database.close()
        }
    }

    @Test
    fun savePersistsItemAndEnqueuesUpsert() = runTest {
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

            val householdEntity = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440001",
                name = "My Household",
                createdBy = userEntity.id,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            val itemEntity = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440002",
                householdId = householdEntity.id,
                name = "Item 1",
                categoryId = "550e8400-e29b-41d4-a716-446655440003",
                brand = "Brand 1",
                model = "Model 1",
                serialNumber = "Serial 1",
                purchaseDateEpochDay = 1_000L,
                purchasePriceMinorUnits = 1_000L,
                purchaseCurrencyCode = "USD",
                purchaseSeller = "Seller 1",
                statusCode = "active",
                isFavorite = true,
                isArchived = false,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            database.userDao().upsert(userEntity)
            database.householdDao().upsert(householdEntity)

            val item = itemEntity.toDomain()

            val enqueuedAt = Instant.parse("2026-08-26T10:15:30.000000789Z")
            val mutationId = "550e8400-e29b-41d4-a716-446655440099"

            val fixedClock = object : Clock {
                override fun now() = enqueuedAt
            }
            val repository = RoomItemRepository(
                database = database,
                clock = fixedClock,
                generateMutationId = { mutationId },
            )
            repository.save(item)

            val storedItem = database.itemDao().findById(item.id.value)

            val storedSync = database.syncOutboxDao().findByTarget(
                scopeId = item.householdId.value,
                recordTypeCode = SyncRecordType.ITEM.code,
                recordId = item.id.value,
            )

            assertEquals(
                item.toEntity(),
                storedItem,
            )

            assertEquals(
                SyncOutboxEntity(
                    scopeId = item.householdId.value,
                    recordTypeCode = SyncRecordType.ITEM.code,
                    recordId = item.id.value,
                    mutationId = mutationId,
                    operationCode = SyncOperation.UPSERT.code,
                    baseRemoteVersion = null,
                    enqueuedAtEpochSeconds = enqueuedAt.epochSeconds,
                    enqueuedAtNanoseconds = enqueuedAt.nanosecondsOfSecond,
                ),
                storedSync,
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun saveRollsBackItemWhenOutboxWriteFails() = runTest {
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

            val householdEntity = HouseholdEntity(
                id = "550e8400-e29b-41d4-a716-446655440001",
                name = "My Household",
                createdBy = userEntity.id,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            val itemEntity = ItemEntity(
                id = "550e8400-e29b-41d4-a716-446655440002",
                householdId = householdEntity.id,
                name = "Item 1",
                categoryId = "550e8400-e29b-41d4-a716-446655440003",
                brand = "Brand 1",
                model = "Model 1",
                serialNumber = "Serial 1",
                purchaseDateEpochDay = 1_000L,
                purchasePriceMinorUnits = 1_000L,
                purchaseCurrencyCode = "USD",
                purchaseSeller = "Seller 1",
                statusCode = "active",
                isFavorite = true,
                isArchived = false,
                createdAtEpochSeconds = 1_000L,
                createdAtNanoseconds = 400,
                updatedAtEpochSeconds = 1_000L,
                updatedAtNanoseconds = 500,
            )

            database.userDao().upsert(userEntity)
            database.householdDao().upsert(householdEntity)
            val item = itemEntity.toDomain()

            val repository = RoomItemRepository(
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
                repository.save(item)
            }

            assertNull(
                database.itemDao().findById(item.id.value),
            )

            assertNull(
                database.syncOutboxDao().findByTarget(
                    scopeId = item.householdId.value,
                    recordTypeCode = SyncRecordType.ITEM.code,
                    recordId = item.id.value,
                ),
            )
        } finally {
            database.close()
        }
    }
}
