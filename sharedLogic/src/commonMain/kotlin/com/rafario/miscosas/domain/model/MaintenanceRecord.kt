package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class MaintenanceRecord(
    val id: MaintenanceRecordId,
    val taskId: MaintenanceTaskId,
    val completedOn: LocalDate,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        if (notes != null) {
            require(notes.isNotBlank()) {
                "MaintenanceRecord notes must not be blank"
            }
        }

        require(updatedAt >= createdAt) {
            "MaintenanceRecord updatedAt must not be before createdAt"
        }
    }
}
