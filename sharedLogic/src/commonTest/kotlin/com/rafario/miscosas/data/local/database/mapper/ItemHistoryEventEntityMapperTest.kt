package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.ItemHistoryEventEntity
import com.rafario.miscosas.domain.model.ItemHistoryEvent
import com.rafario.miscosas.domain.model.ItemHistoryEventId
import com.rafario.miscosas.domain.model.ItemHistoryEventType
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.ItemStatus
import com.rafario.miscosas.domain.model.ItemStatusChange
import com.rafario.miscosas.domain.model.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class ItemHistoryEventEntityMapperTest {

    @Test
    fun mapsStatusChangedEventToEntityPreservingAllFieldsAndNanoseconds() {
        val event = createStatusChangedEvent()

        val entity = event.toEntity()

        assertEquals(event.id.value, entity.id)
        assertEquals(event.itemId.value, entity.itemId)
        assertEquals(event.type.code, entity.typeCode)
        assertEquals(event.occurredAt.epochSeconds, entity.occurredAtEpochSeconds)
        assertEquals(event.occurredAt.nanosecondsOfSecond, entity.occurredAtNanoseconds)
        assertEquals(event.recordedBy.value, entity.recordedByUserId)
        assertEquals(event.statusChange?.previousStatus?.code, entity.previousStatusCode)
        assertEquals(event.statusChange?.newStatus?.code, entity.newStatusCode)
        assertEquals(event.details, entity.details)
    }

    @Test
    fun mapsStatusChangedEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val entity = createStatusChangedEntity()

        val event = entity.toDomain()

        assertEquals(ItemHistoryEventId(entity.id), event.id)
        assertEquals(ItemId(entity.itemId), event.itemId)
        assertEquals(ItemHistoryEventType.STATUS_CHANGED, event.type)
        assertEquals(
            Instant.fromEpochSeconds(
                epochSeconds = entity.occurredAtEpochSeconds,
                nanosecondAdjustment = entity.occurredAtNanoseconds.toLong(),
            ),
            event.occurredAt,
        )
        assertEquals(UserId(entity.recordedByUserId), event.recordedBy)
        assertEquals(
            ItemStatusChange(ItemStatus.ACTIVE, ItemStatus.SOLD),
            event.statusChange,
        )
        assertEquals(entity.details, event.details)
    }

    @Test
    fun preservesAbsentStatusChangeAndDetailsForRegularEvent() {
        val entity = createStatusChangedEntity(
            typeCode = ItemHistoryEventType.CREATED.code,
            previousStatusCode = null,
            newStatusCode = null,
            details = null,
        )

        val event = entity.toDomain()
        val mappedEntity = event.toEntity()

        assertNull(event.statusChange)
        assertNull(event.details)
        assertNull(mappedEntity.previousStatusCode)
        assertNull(mappedEntity.newStatusCode)
        assertNull(mappedEntity.details)
    }

    @Test
    fun rejectsUnknownStoredEventTypeCode() {
        val entity = createStatusChangedEntity(typeCode = "unknown-event")

        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsIncompleteStoredStatusChange() {
        val entity = createStatusChangedEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(previousStatusCode = null).toDomain()
        }
        assertFailsWith<IllegalStateException> {
            entity.copy(newStatusCode = null).toDomain()
        }
    }

    @Test
    fun rejectsUnknownStoredStatusCodes() {
        val entity = createStatusChangedEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(previousStatusCode = "unknown-status").toDomain()
        }
        assertFailsWith<IllegalStateException> {
            entity.copy(newStatusCode = "unknown-status").toDomain()
        }
    }

    @Test
    fun rejectsStatusCodesThatDoNotMatchEventType() {
        val statusChangedEntity = createStatusChangedEntity()

        assertFailsWith<IllegalStateException> {
            statusChangedEntity.copy(
                previousStatusCode = null,
                newStatusCode = null,
            ).toDomain()
        }
        assertFailsWith<IllegalStateException> {
            statusChangedEntity.copy(
                typeCode = ItemHistoryEventType.CREATED.code,
            ).toDomain()
        }
    }

    @Test
    fun rejectsStoredStatusChangeWithEqualStatuses() {
        val entity = createStatusChangedEntity(
            previousStatusCode = ItemStatus.ACTIVE.code,
            newStatusCode = ItemStatus.ACTIVE.code,
        )

        assertFailsWith<IllegalArgumentException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val entity = createStatusChangedEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(occurredAtNanoseconds = -1).toDomain()
        }
        assertFailsWith<IllegalStateException> {
            entity.copy(occurredAtNanoseconds = 1_000_000_000).toDomain()
        }
    }

    private fun createStatusChangedEvent(): ItemHistoryEvent = ItemHistoryEvent(
        id = ItemHistoryEventId("550e8400-e29b-41d4-a716-446655440030"),
        itemId = ItemId("550e8400-e29b-41d4-a716-446655440010"),
        type = ItemHistoryEventType.STATUS_CHANGED,
        occurredAt = Instant.parse("2026-08-24T10:30:00.000000400Z"),
        recordedBy = UserId("firebase-user_A1b2C3"),
        statusChange = ItemStatusChange(
            previousStatus = ItemStatus.ACTIVE,
            newStatus = ItemStatus.SOLD,
        ),
        details = "Vendido a un particular",
    )

    private fun createStatusChangedEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440030",
        itemId: String = "550e8400-e29b-41d4-a716-446655440010",
        typeCode: String = ItemHistoryEventType.STATUS_CHANGED.code,
        occurredAtEpochSeconds: Long = 1_777_030_200L,
        occurredAtNanoseconds: Int = 400,
        recordedByUserId: String = "firebase-user_A1b2C3",
        previousStatusCode: String? = ItemStatus.ACTIVE.code,
        newStatusCode: String? = ItemStatus.SOLD.code,
        details: String? = "Vendido a un particular",
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
}
