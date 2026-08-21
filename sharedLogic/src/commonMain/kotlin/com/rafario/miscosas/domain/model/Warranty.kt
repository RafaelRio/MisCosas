package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import kotlinx.datetime.daysUntil

data class Warranty(
    val id: WarrantyId,
    val itemId: ItemId,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val type: WarrantyType?,
    val providerName: String?,
    val notes: String?,
    val reminderDaysBeforeEnd: Int?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        if (startDate != null && endDate != null) {
            require(endDate >= startDate) {
                "Warranty endDate must not be before startDate"
            }
        }

        if (providerName != null) {
            require(providerName.isNotBlank()) {
                "Warranty providerName must not be blank"
            }
        }

        if (notes != null) {
            require(notes.isNotBlank()) {
                "Warranty notes must not be blank"
            }
        }

        if (reminderDaysBeforeEnd != null) {
            require(reminderDaysBeforeEnd >= 0) {
                "Warranty reminderDaysBeforeEnd must be zero or greater"
            }

            require(endDate != null) {
                "Warranty reminder requires a known endDate"
            }
        }

        require(startDate != null || endDate != null || type != null || providerName != null || notes != null) {
            "Warranty must contain at least one meaningful field"
        }

        require(updatedAt >= createdAt) {
            "Warranty updatedAt must not be before createdAt"
        }
    }

    fun statusOn(
        date: LocalDate,
        expiringSoonDays: Int,
    ): WarrantyStatus {
        require(expiringSoonDays >= 0) {
            "Warranty expiringSoonDays must be zero or greater"
        }

        val knownEndDate = endDate
            ?: return WarrantyStatus.UNKNOWN

        val daysUntilEnd = date.daysUntil(knownEndDate)

        return when {
            daysUntilEnd < 0 -> WarrantyStatus.EXPIRED
            daysUntilEnd <= expiringSoonDays -> WarrantyStatus.EXPIRING_SOON
            else -> WarrantyStatus.ACTIVE
        }
    }
}
