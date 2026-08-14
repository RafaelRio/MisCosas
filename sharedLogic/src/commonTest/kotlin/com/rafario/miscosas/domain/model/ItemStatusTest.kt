package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ItemStatusTest {

    @Test
    fun exposesExpectedCodesInDisplayOrder() {
        val codes = ItemStatus.entries.map { status ->
            status.code
        }

        assertEquals(
            listOf(
                "active",
                "sold",
                "gifted",
                "broken",
                "lost",
                "recycled",
            ),
            codes,
        )
    }

    @Test
    fun findsStatusByCode() {
        val status = ItemStatus.fromCodeOrNull("sold")

        assertEquals(ItemStatus.SOLD, status)
    }

    @Test
    fun returnsNullForUnknownCode() {
        val status = ItemStatus.fromCodeOrNull("archived")

        assertNull(status)
    }
}