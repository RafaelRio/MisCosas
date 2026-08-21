package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class ItemHistoryEventTest {

    @Test
    fun keepsItemHistoryEventInformation() {
        val id = ItemHistoryEventId(
            "550e8400-e29b-41d4-a716-446655440000",
        )
        val itemId = ItemId(
            "550e8400-e29b-41d4-a716-446655440001",
        )
        val type = ItemHistoryEventType.STATUS_CHANGED
        val occurredAt = Instant.parse("2026-08-21T08:00:00Z")
        val recordedBy = UserId("firebase-user_A1b2C3")
        val statusChange = ItemStatusChange(
            previousStatus = ItemStatus.ACTIVE,
            newStatus = ItemStatus.SOLD,
        )
        val details = "Venta realizada en persona"

        val event = ItemHistoryEvent(
            id = id,
            itemId = itemId,
            type = type,
            occurredAt = occurredAt,
            recordedBy = recordedBy,
            statusChange = statusChange,
            details = details,
        )

        assertEquals(id, event.id)
        assertEquals(itemId, event.itemId)
        assertEquals(type, event.type)
        assertEquals(occurredAt, event.occurredAt)
        assertEquals(recordedBy, event.recordedBy)
        assertEquals(statusChange, event.statusChange)
        assertEquals(details, event.details)
    }

    @Test
    fun allowsMissingDetails() {
        val event = createItemHistoryEvent(details = null)

        assertNull(event.details)
    }

    @Test
    fun rejectsBlankDetailsWhenPresent() {
        assertFailsWith<IllegalArgumentException> {
            createItemHistoryEvent(details = "   ")
        }
    }

    @Test
    fun requiresStatusChangeForStatusChangedEvent() {
        assertFailsWith<IllegalArgumentException> {
            createItemHistoryEvent(
                type = ItemHistoryEventType.STATUS_CHANGED,
                statusChange = null,
            )
        }
    }

    @Test
    fun rejectsStatusChangeForOtherEventTypes() {
        assertFailsWith<IllegalArgumentException> {
            createItemHistoryEvent(
                type = ItemHistoryEventType.CREATED,
                statusChange = ItemStatusChange(
                    previousStatus = ItemStatus.ACTIVE,
                    newStatus = ItemStatus.SOLD,
                ),
            )
        }
    }

    @Test
    fun allowsMissingStatusChangeForOtherEventTypes() {
        val event = createItemHistoryEvent(
            type = ItemHistoryEventType.CREATED,
            statusChange = null,
        )

        assertNull(event.statusChange)
    }

    private fun createItemHistoryEvent(
        id: ItemHistoryEventId =
            ItemHistoryEventId("550e8400-e29b-41d4-a716-446655440000"),
        itemId: ItemId =
            ItemId("550e8400-e29b-41d4-a716-446655440001"),
        type: ItemHistoryEventType = ItemHistoryEventType.STATUS_CHANGED,
        occurredAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        recordedBy: UserId = UserId("firebase-user_A1b2C3"),
        statusChange: ItemStatusChange? = ItemStatusChange(
            previousStatus = ItemStatus.ACTIVE,
            newStatus = ItemStatus.SOLD,
        ),
        details: String? = "Venta realizada en persona",
    ): ItemHistoryEvent {
        return ItemHistoryEvent(
            id = id,
            itemId = itemId,
            type = type,
            occurredAt = occurredAt,
            recordedBy = recordedBy,
            statusChange = statusChange,
            details = details,
        )
    }
}