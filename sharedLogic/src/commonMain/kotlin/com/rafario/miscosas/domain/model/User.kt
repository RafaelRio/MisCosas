package com.rafario.miscosas.domain.model

import kotlin.time.Instant

data class User(
    val id: UserId,
    val displayName: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(displayName.isNotBlank()) {
            "User displayName must not be blank"
        }

        require(updatedAt >= createdAt) {
            "User updatedAt must not be before createdAt"
        }
    }
}