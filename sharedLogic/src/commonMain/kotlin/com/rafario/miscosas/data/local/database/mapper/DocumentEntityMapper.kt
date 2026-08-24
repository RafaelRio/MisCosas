package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.DocumentEntity
import com.rafario.miscosas.domain.model.Document
import com.rafario.miscosas.domain.model.DocumentId
import com.rafario.miscosas.domain.model.DocumentType
import com.rafario.miscosas.domain.model.ItemId

internal fun Document.toEntity(): DocumentEntity {
    return DocumentEntity(
        id = id.value,
        itemId = itemId.value,
        typeCode = type.code,
        fileName = fileName,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        createdAtEpochSeconds = createdAt.epochSeconds,
        createdAtNanoseconds = createdAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
    )
}

internal fun DocumentEntity.toDomain(): Document {
    val documentType = checkNotNull(DocumentType.fromCodeOrNull(typeCode)) {
        "DocumentType with code $typeCode not found"
    }
    return Document(
        id = DocumentId(id),
        itemId = ItemId(itemId),
        type = documentType,
        fileName = fileName,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        createdAt = instantFromEpochColumns(
            epochSeconds = createdAtEpochSeconds,
            nanosecondsOfSecond = createdAtNanoseconds,
            fieldName = "DocumentEntity.createdAt"
        ),
        updatedAt = instantFromEpochColumns(
            epochSeconds = updatedAtEpochSeconds,
            nanosecondsOfSecond = updatedAtNanoseconds,
            fieldName = "DocumentEntity.updatedAt"
        ),
    )
}