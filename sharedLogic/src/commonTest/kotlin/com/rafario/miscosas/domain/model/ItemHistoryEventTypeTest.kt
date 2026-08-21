package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ItemHistoryEventTypeTest {

    @Test
    fun hasStableCodes() {
        assertEquals("created", ItemHistoryEventType.CREATED.code)
        assertEquals(
            "purchase_recorded",
            ItemHistoryEventType.PURCHASE_RECORDED.code,
        )
        assertEquals(
            "warranty_added",
            ItemHistoryEventType.WARRANTY_ADDED.code,
        )
        assertEquals(
            "document_added",
            ItemHistoryEventType.DOCUMENT_ADDED.code,
        )
        assertEquals(
            "maintenance_completed",
            ItemHistoryEventType.MAINTENANCE_COMPLETED.code,
        )
        assertEquals(
            "repair_recorded",
            ItemHistoryEventType.REPAIR_RECORDED.code,
        )
        assertEquals(
            "status_changed",
            ItemHistoryEventType.STATUS_CHANGED.code,
        )
        assertEquals("archived", ItemHistoryEventType.ARCHIVED.code)
        assertEquals("unarchived", ItemHistoryEventType.UNARCHIVED.code)
    }

    @Test
    fun findsItemHistoryEventTypeByCode() {
        val type = ItemHistoryEventType.fromCodeOrNull(
            "maintenance_completed",
        )

        assertEquals(ItemHistoryEventType.MAINTENANCE_COMPLETED, type)
    }

    @Test
    fun returnsNullForUnknownCode() {
        val type = ItemHistoryEventType.fromCodeOrNull("unknown")

        assertNull(type)
    }
}