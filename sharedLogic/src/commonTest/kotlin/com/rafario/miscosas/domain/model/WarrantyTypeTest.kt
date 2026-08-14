package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WarrantyTypeTest {

    @Test
    fun hasStableCodes() {
        assertEquals("legal", WarrantyType.LEGAL.code)
        assertEquals("commercial", WarrantyType.COMMERCIAL.code)
        assertEquals("extended", WarrantyType.EXTENDED.code)
        assertEquals("other", WarrantyType.OTHER.code)
    }

    @Test
    fun findsWarrantyTypeByCode() {
        val warrantyType = WarrantyType.fromCodeOrNull("extended")
        assertEquals(WarrantyType.EXTENDED, warrantyType)
    }

    @Test
    fun returnsNullForUnknownCode() {
        val warrantyType = WarrantyType.fromCodeOrNull("unknown")
        assertNull(warrantyType)
    }
}