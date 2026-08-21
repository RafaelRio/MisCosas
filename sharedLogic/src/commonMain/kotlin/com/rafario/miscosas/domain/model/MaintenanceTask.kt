package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.time.Instant

data class MaintenanceTask(
    val id: MaintenanceTaskId,
    val itemId: ItemId,
    val name: String,
    val details: String?,
    val interval: MaintenanceInterval,
    val firstDueDate: LocalDate,
    val reminderDaysBeforeDue: Int?,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(name.isNotBlank()) {
            "MaintenanceTask name must not be blank"
        }

        if (details != null) {
            require(details.isNotBlank()) {
                "MaintenanceTask details must not be blank"
            }
        }

        if (notes != null) {
            require(notes.isNotBlank()) {
                "MaintenanceTask notes must not be blank"
            }
        }

        if (reminderDaysBeforeDue != null) {
            require(reminderDaysBeforeDue >= 0) {
                "MaintenanceTask reminderDaysBeforeDue must be zero or greater"
            }
        }

        require(updatedAt >= createdAt) {
            "MaintenanceTask updatedAt must not be before createdAt"
        }
    }

    fun nextDueDate(lastCompletedOn: LocalDate?): LocalDate {
        if (lastCompletedOn == null) {
            return firstDueDate
        }

        return interval.nextDateAfter(lastCompletedOn)
    }

    fun daysUntilDueOn(date: LocalDate, lastCompletedOn: LocalDate?): Int {
        val dueDate = nextDueDate(lastCompletedOn)
        return date.daysUntil(dueDate)
    }

    fun statusOn(
        date: LocalDate,
        lastCompletedOn: LocalDate?,
    ): MaintenanceStatus {
        val daysUntilDue = daysUntilDueOn(
            date = date,
            lastCompletedOn = lastCompletedOn,
        )

        return when {
            daysUntilDue > 0 -> MaintenanceStatus.UPCOMING
            daysUntilDue == 0 -> MaintenanceStatus.DUE
            else -> MaintenanceStatus.OVERDUE
        }
    }
}
