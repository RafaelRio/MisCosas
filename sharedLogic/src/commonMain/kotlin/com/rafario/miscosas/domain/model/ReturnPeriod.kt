package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.time.Instant

data class ReturnPeriod(
    val itemId: ItemId,
    val deadline: LocalDate,
    val seller: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val trackingState: ReturnTrackingState
) {
    init {
        require(seller == null || seller.isNotBlank()) {
            "Return period seller must not be blank"
        }

        require(updatedAt >= createdAt) {
            "Return period updatedAt must not be before createdAt"
        }
    }

    fun daysRemainingOn(date: LocalDate): Int {
        return date.daysUntil(deadline)
    }

    fun statusOn(
        date: LocalDate,
        endingSoonThresholdDays: Int,
    ): ReturnWindowStatus {
        require(endingSoonThresholdDays >= 0) {
            "Return period endingSoonDays must be zero or greater"
        }

        val daysRemaining = daysRemainingOn(date)

        return when {
            daysRemaining < 0 -> ReturnWindowStatus.EXPIRED
            daysRemaining <= endingSoonThresholdDays -> ReturnWindowStatus.ENDING_SOON
            else -> ReturnWindowStatus.OPEN
        }
    }
}
