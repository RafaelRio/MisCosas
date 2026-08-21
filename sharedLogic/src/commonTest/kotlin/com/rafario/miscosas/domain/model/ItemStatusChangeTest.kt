package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ItemStatusChangeTest {

    @Test
    fun keepsPreviousAndNewStatus() {
        val change = ItemStatusChange(
            previousStatus = ItemStatus.ACTIVE,
            newStatus = ItemStatus.SOLD,
        )

        assertEquals(ItemStatus.ACTIVE, change.previousStatus)
        assertEquals(ItemStatus.SOLD, change.newStatus)
    }

    @Test
    fun rejectsChangeToSameStatus() {
        assertFailsWith<IllegalArgumentException> {
            ItemStatusChange(
                previousStatus = ItemStatus.ACTIVE,
                newStatus = ItemStatus.ACTIVE,
            )
        }
    }
}