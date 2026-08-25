package com.rafario.miscosas.data.sync.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SyncOperationTest {

    @Test
    fun hasStableCodes() {
        assertEquals("upsert", SyncOperation.UPSERT.code)
        assertEquals("delete", SyncOperation.DELETE.code)
    }

    @Test
    fun findsOperationByCode() {
        assertEquals(SyncOperation.UPSERT, SyncOperation.fromCodeOrNull("upsert"))
        assertEquals(SyncOperation.DELETE, SyncOperation.fromCodeOrNull("delete"))
    }

    @Test
    fun returnsNullForUnknownCode() {
        assertNull(SyncOperation.fromCodeOrNull("unknown"))
    }
}
