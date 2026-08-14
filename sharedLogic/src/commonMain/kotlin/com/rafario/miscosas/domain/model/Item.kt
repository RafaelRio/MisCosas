package com.rafario.miscosas.domain.model

import kotlin.time.Instant

data class Item(
    val id: ItemId,
    val householdId: HouseholdId,
    val name: String,
    val categoryId: CategoryId,
    val brand: String?,
    val model: String?,
    val serialNumber: String?,
    val purchase: Purchase?,
    val status: ItemStatus,
    val isFavorite: Boolean,
    val isArchived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(name.isNotBlank()) {
            "Item name must not be blank"
        }

        require(updatedAt >= createdAt) {
            "Item updatedAt must not be before createdAt"
        }

        requireNotBlankIfPresent(
            value = brand,
            fieldName = "brand",
        )
        requireNotBlankIfPresent(
            value = model,
            fieldName = "model",
        )
        requireNotBlankIfPresent(
            value = serialNumber,
            fieldName = "serialNumber",
        )
    }

    private fun requireNotBlankIfPresent(
        value: String?,
        fieldName: String,
    ) {
        require(value == null || value.isNotBlank()) {
            "Item $fieldName must not be blank"
        }
    }
}
