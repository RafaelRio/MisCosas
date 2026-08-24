package com.rafario.miscosas.data.local.database.dao

import androidx.sqlite.SQLiteException
import com.rafario.miscosas.data.local.database.MisCosasDatabase
import com.rafario.miscosas.data.local.database.createTestDatabase
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.ItemEntity
import com.rafario.miscosas.data.local.database.entity.ItemHistoryEventEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.domain.model.ItemHistoryEventType
import com.rafario.miscosas.domain.model.ItemStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ItemHistoryEventDaoTest {

    @Test
    fun insertsAndFindsItemHistoryEventById() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)
            val event = createItemHistoryEventEntity(
                itemId = parents.item.id,
                recordedByUserId = parents.user.id,
            )

            database.itemHistoryEventDao().insert(event)

            assertEquals(
                event,
                database.itemHistoryEventDao().findById(event.id),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsNullWhenItemHistoryEventDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            assertNull(database.itemHistoryEventDao().findById("missing-event"))
        } finally {
            database.close()
        }
    }

    @Test
    fun findsEventsByItemIdInReverseChronologicalOrder() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)
            val otherItem = parents.item.copy(
                id = "550e8400-e29b-41d4-a716-446655440011",
                name = "Bicicleta",
            )
            database.itemDao().upsert(otherItem)

            val oldestEvent = createItemHistoryEventEntity(
                id = "550e8400-e29b-41d4-a716-446655440039",
                itemId = parents.item.id,
                recordedByUserId = parents.user.id,
                occurredAtEpochSeconds = 1_000L,
                occurredAtNanoseconds = 900,
            )
            val lowerNanosecondsEvent = createItemHistoryEventEntity(
                id = "550e8400-e29b-41d4-a716-446655440038",
                itemId = parents.item.id,
                recordedByUserId = parents.user.id,
                occurredAtEpochSeconds = 1_001L,
                occurredAtNanoseconds = 100,
            )
            val sameInstantLowerIdEvent = createItemHistoryEventEntity(
                id = "550e8400-e29b-41d4-a716-446655440030",
                itemId = parents.item.id,
                recordedByUserId = parents.user.id,
                occurredAtEpochSeconds = 1_001L,
                occurredAtNanoseconds = 200,
            )
            val sameInstantHigherIdEvent = createItemHistoryEventEntity(
                id = "550e8400-e29b-41d4-a716-446655440031",
                itemId = parents.item.id,
                recordedByUserId = parents.user.id,
                occurredAtEpochSeconds = 1_001L,
                occurredAtNanoseconds = 200,
            )
            val otherItemEvent = createItemHistoryEventEntity(
                id = "550e8400-e29b-41d4-a716-446655440032",
                itemId = otherItem.id,
                recordedByUserId = parents.user.id,
                occurredAtEpochSeconds = 2_000L,
            )

            listOf(
                oldestEvent,
                lowerNanosecondsEvent,
                sameInstantLowerIdEvent,
                sameInstantHigherIdEvent,
                otherItemEvent,
            ).forEach { database.itemHistoryEventDao().insert(it) }

            assertEquals(
                listOf(
                    sameInstantHigherIdEvent,
                    sameInstantLowerIdEvent,
                    lowerNanosecondsEvent,
                    oldestEvent,
                ),
                database.itemHistoryEventDao().findAllByItemId(parents.item.id),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun returnsEmptyListWhenItemHasNoHistoryEvents() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)

            assertEquals(
                emptyList(),
                database.itemHistoryEventDao().findAllByItemId(parents.item.id),
            )
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsEventWhenItemDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)
            val event = createItemHistoryEventEntity(
                itemId = "missing-item",
                recordedByUserId = parents.user.id,
            )

            assertFailsWith<SQLiteException> {
                database.itemHistoryEventDao().insert(event)
            }
            assertNull(database.itemHistoryEventDao().findById(event.id))
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsEventWhenRecordedUserDoesNotExist() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)
            val event = createItemHistoryEventEntity(
                itemId = parents.item.id,
                recordedByUserId = "missing-user",
            )

            assertFailsWith<SQLiteException> {
                database.itemHistoryEventDao().insert(event)
            }
            assertNull(database.itemHistoryEventDao().findById(event.id))
        } finally {
            database.close()
        }
    }

    @Test
    fun rejectsDuplicateEventIdWithoutOverwritingOriginal() = runTest {
        val database = createTestDatabase()

        try {
            val parents = insertParents(database)
            val originalEvent = createItemHistoryEventEntity(
                itemId = parents.item.id,
                recordedByUserId = parents.user.id,
            )
            val conflictingEvent = originalEvent.copy(
                details = "Contenido que no debe reemplazar el original",
            )

            database.itemHistoryEventDao().insert(originalEvent)

            assertFailsWith<SQLiteException> {
                database.itemHistoryEventDao().insert(conflictingEvent)
            }
            assertEquals(
                originalEvent,
                database.itemHistoryEventDao().findById(originalEvent.id),
            )
        } finally {
            database.close()
        }
    }

    private suspend fun insertParents(database: MisCosasDatabase): HistoryParents {
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
            id = "550e8400-e29b-41d4-a716-446655440010",
            householdId = household.id,
            name = "Portátil",
            categoryId = "builtin:technology",
            brand = "Framework",
            model = "Laptop 13",
            serialNumber = "FW-123",
            purchaseDateEpochDay = null,
            purchasePriceMinorUnits = null,
            purchaseCurrencyCode = null,
            purchaseSeller = null,
            statusCode = ItemStatus.ACTIVE.code,
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

        return HistoryParents(user = user, item = item)
    }

    private fun createItemHistoryEventEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440030",
        itemId: String,
        typeCode: String = ItemHistoryEventType.CREATED.code,
        occurredAtEpochSeconds: Long = 1_001L,
        occurredAtNanoseconds: Int = 400,
        recordedByUserId: String,
        previousStatusCode: String? = null,
        newStatusCode: String? = null,
        details: String? = "Objeto añadido al hogar",
    ): ItemHistoryEventEntity = ItemHistoryEventEntity(
        id = id,
        itemId = itemId,
        typeCode = typeCode,
        occurredAtEpochSeconds = occurredAtEpochSeconds,
        occurredAtNanoseconds = occurredAtNanoseconds,
        recordedByUserId = recordedByUserId,
        previousStatusCode = previousStatusCode,
        newStatusCode = newStatusCode,
        details = details,
    )

    private data class HistoryParents(
        val user: UserEntity,
        val item: ItemEntity,
    )
}
