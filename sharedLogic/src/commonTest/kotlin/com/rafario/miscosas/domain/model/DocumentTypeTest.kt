package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DocumentTypeTest {

    @Test
    fun hasStableCodes() {
        assertEquals("receipt", DocumentType.RECEIPT.code)
        assertEquals("invoice", DocumentType.INVOICE.code)
        assertEquals("warranty", DocumentType.WARRANTY.code)
        assertEquals("manual", DocumentType.MANUAL.code)
        assertEquals("other", DocumentType.OTHER.code)
    }

    @Test
    fun findsDocumentTypeByCode() {
        val warrantyType = DocumentType.fromCodeOrNull("receipt")
        assertEquals(DocumentType.RECEIPT, warrantyType)
    }

    @Test
    fun returnsNullForUnknownCode() {
        val warrantyType = DocumentType.fromCodeOrNull("unknown")
        assertNull(warrantyType)
    }
}