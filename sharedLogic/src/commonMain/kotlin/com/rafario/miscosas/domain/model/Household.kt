package com.rafario.miscosas.domain.model

import kotlin.time.Instant

data class Household(
    val id: HouseholdId,
    val name: String,
    val createdBy: UserId,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(name.isNotBlank()) {
            "Household name must not be blank"
        }

        require(updatedAt >= createdAt) {
            "Household updatedAt must not be before createdAt"
        }
    }
}
