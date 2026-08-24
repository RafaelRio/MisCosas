package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.DocumentEntity
import com.rafario.miscosas.domain.model.Document
import com.rafario.miscosas.domain.model.DocumentId
import com.rafario.miscosas.domain.model.DocumentType
import com.rafario.miscosas.domain.model.ItemId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class DocumentEntityMapperTest {

    @Test
    fun mapsDocumentToEntityPreservingAllFieldsAndNanoseconds() {
        val document = Document(
            id = DocumentId("550e8400-e29b-41d4-a716-446655440000"),
            itemId = ItemId("550e8400-e29b-41d4-a716-446655440010"),
            type = DocumentType.MANUAL,
            fileName = "image.jpg",
            mimeType = "image/jpeg",
            sizeBytes = 1024,
            createdAt = Instant.parse("2023-01-01T00:00:00.000000400Z"),
            updatedAt = Instant.parse("2023-01-01T00:00:00.000000500Z"),
        )

        val documentEntity = document.toEntity()

        assertEquals(document.id.value, documentEntity.id)
        assertEquals(document.itemId.value, documentEntity.itemId)
        assertEquals(document.type.code, documentEntity.typeCode)
        assertEquals(document.fileName, documentEntity.fileName)
        assertEquals(document.mimeType, documentEntity.mimeType)
        assertEquals(document.sizeBytes, documentEntity.sizeBytes)
        assertEquals(document.createdAt.epochSeconds, documentEntity.createdAtEpochSeconds)
        assertEquals(document.createdAt.nanosecondsOfSecond, documentEntity.createdAtNanoseconds)
        assertEquals(document.updatedAt.epochSeconds, documentEntity.updatedAtEpochSeconds)
        assertEquals(document.updatedAt.nanosecondsOfSecond, documentEntity.updatedAtNanoseconds)
    }

    @Test
    fun mapsDocumentEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val documentEntity = createDocumentEntity()

        val domainDocument = documentEntity.toDomain()

        assertEquals(documentEntity.id, domainDocument.id.value)
        assertEquals(documentEntity.itemId, domainDocument.itemId.value)
        assertEquals(documentEntity.typeCode, domainDocument.type.code)
        assertEquals(documentEntity.fileName, domainDocument.fileName)
        assertEquals(documentEntity.mimeType, domainDocument.mimeType)
        assertEquals(documentEntity.sizeBytes, domainDocument.sizeBytes)
        assertEquals(documentEntity.createdAtEpochSeconds, domainDocument.createdAt.epochSeconds)
        assertEquals(documentEntity.createdAtNanoseconds, domainDocument.createdAt.nanosecondsOfSecond)
        assertEquals(documentEntity.updatedAtEpochSeconds, domainDocument.updatedAt.epochSeconds)
        assertEquals(documentEntity.updatedAtNanoseconds, domainDocument.updatedAt.nanosecondsOfSecond)
    }

    @Test
    fun rejectsUnknownStoredDocumentTypeCode() {
        val entity = createDocumentEntity(typeCode = "unknown-type")

        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val entity = createDocumentEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(createdAtNanoseconds = -1).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(updatedAtNanoseconds = 1_000_000_000).toDomain()
        }
    }

    private fun createDocumentEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440000",
        itemId: String = "550e8400-e29b-41d4-a716-446655440010",
        typeCode: String = DocumentType.INVOICE.code,
        fileName: String = "image.jpg",
        mimeType: String = "image/jpeg",
        sizeBytes: Long = 1_024L,
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 400,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 500,
    ): DocumentEntity {
        return DocumentEntity(
            id = id,
            itemId = itemId,
            typeCode = typeCode,
            fileName = fileName,
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            createdAtEpochSeconds = createdAtEpochSeconds,
            createdAtNanoseconds = createdAtNanoseconds,
            updatedAtEpochSeconds = updatedAtEpochSeconds,
            updatedAtNanoseconds = updatedAtNanoseconds,
        )
    }
}
