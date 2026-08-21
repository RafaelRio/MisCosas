package com.rafario.miscosas.domain.model

import kotlin.time.Instant

data class Document(
    val id: DocumentId,
    val itemId: ItemId,
    val type: DocumentType,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(fileName.isNotBlank()) {
            "Document fileName must not be blank"
        }

        require(sizeBytes > 0) {
            "Document sizeBytes must be greater than zero"
        }

        require(hasValidMimeTypeFormat(mimeType)) {
            "Document mimeType must be a valid MIME type"
        }

        require(updatedAt >= createdAt) {
            "Document updatedAt must not be before createdAt"
        }
    }
}

private fun hasValidMimeTypeFormat(value: String): Boolean {
    val parts = value.split('/')

    return parts.size == 2 &&
            parts.all { it.isNotBlank() } &&
            value.none { it.isWhitespace() }
}
