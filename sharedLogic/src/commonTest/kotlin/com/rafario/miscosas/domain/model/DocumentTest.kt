package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class DocumentTest {

    @Test
    fun keepsDocumentMetadata() {
        val id = DocumentId("550e8400-e29b-41d4-a716-446655440000")
        val itemId = ItemId("550e8400-e29b-41d4-a716-446655440001")
        val type = DocumentType.INVOICE
        val fileName = "factura-televisor.pdf"
        val mimeType = "application/pdf"
        val sizeBytes = 125_000L
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00Z")

        val document = createDocument(
            id = id,
            itemId = itemId,
            type = type,
            fileName = fileName,
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(id, document.id)
        assertEquals(itemId, document.itemId)
        assertEquals(type, document.type)
        assertEquals(fileName, document.fileName)
        assertEquals(mimeType, document.mimeType)
        assertEquals(sizeBytes, document.sizeBytes)
        assertEquals(createdAt, document.createdAt)
        assertEquals(updatedAt, document.updatedAt)
    }

    @Test
    fun rejectsBlankFileName() {
        assertFailsWith<IllegalArgumentException> {
            createDocument(fileName = "    ")
        }
    }

    @Test
    fun rejectsBlankMimeType() {
        assertFailsWith<IllegalArgumentException> {
            createDocument(mimeType = "    ")
        }
    }

    @Test
    fun rejectsMalformedMimeType() {
        assertFailsWith<IllegalArgumentException> {
            createDocument(mimeType = "applicationpdf")
        }
    }

    @Test
    fun rejectsEmptyFile() {
        assertFailsWith<IllegalArgumentException> {
            createDocument(sizeBytes = 0L)
        }
    }

    @Test
    fun rejectsNegativeFileSize() {
        assertFailsWith<IllegalArgumentException> {
            createDocument(sizeBytes = -1L)
        }
    }

    @Test
    fun rejectsInvalidMimeTypeForm() {
        assertFailsWith<IllegalArgumentException> {
            createDocument(mimeType = "/pdf")
        }
        assertFailsWith<IllegalArgumentException> {
            createDocument(mimeType = "application/")
        }
        assertFailsWith<IllegalArgumentException> {
            createDocument(mimeType = "application//pdf")
        }
        assertFailsWith<IllegalArgumentException> {
            createDocument(mimeType = "application /pdf")
        }
    }

    @Test
    fun allowsUpdatedAtEqualToCreatedAt() {
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T08:00:00Z")
        val document = createDocument(
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(createdAt, document.createdAt)
        assertEquals(updatedAt, document.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-20T08:00:00Z")
        assertFailsWith<IllegalArgumentException> {
            createDocument(
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAtWithinSameMillisecond() {
        val createdAt = Instant.parse("2026-08-21T08:00:00.000000500Z")
        val updatedAt = Instant.parse("2026-08-21T08:00:00.000000400Z")
        assertFailsWith<IllegalArgumentException> {
            createDocument(
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    private fun createDocument(
        id: DocumentId = DocumentId("550e8400-e29b-41d4-a716-446655440000"),
        itemId: ItemId = ItemId("550e8400-e29b-41d4-a716-446655440001"),
        type: DocumentType = DocumentType.INVOICE,
        fileName: String = "factura-televisor.pdf",
        mimeType: String = "application/pdf",
        sizeBytes: Long = 125_000L,
        createdAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        updatedAt: Instant = Instant.parse("2026-08-21T09:00:00Z"),
    ): Document {
        return Document(
            id = id,
            itemId = itemId,
            type = type,
            fileName = fileName,
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
