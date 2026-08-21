package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class MaintenanceRecordTest {

    @Test
    fun keepsMaintenanceRecordInformation() {
        val id = MaintenanceRecordId(
            "550e8400-e29b-41d4-a716-446655440000",
        )
        val taskId = MaintenanceTaskId(
            "550e8400-e29b-41d4-a716-446655440001",
        )
        val completedOn = LocalDate(2026, 8, 21)
        val notes = "Filtros lavados y secados"
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00Z")

        val record = MaintenanceRecord(
            id = id,
            taskId = taskId,
            completedOn = completedOn,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(id, record.id)
        assertEquals(taskId, record.taskId)
        assertEquals(completedOn, record.completedOn)
        assertEquals(notes, record.notes)
        assertEquals(createdAt, record.createdAt)
        assertEquals(updatedAt, record.updatedAt)
    }

    @Test
    fun allowsMissingNotes() {
        val record = createMaintenanceRecord(notes = null)

        assertNull(record.notes)
    }

    @Test
    fun rejectsBlankNotesWhenPresent() {
        assertFailsWith<IllegalArgumentException> {
            createMaintenanceRecord(notes = "   ")
        }
    }

    @Test
    fun allowsUpdatedAtEqualToCreatedAt() {
        val instant = Instant.parse("2026-08-21T08:00:00Z")

        val record = createMaintenanceRecord(
            createdAt = instant,
            updatedAt = instant,
        )

        assertEquals(instant, record.createdAt)
        assertEquals(instant, record.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-20T08:00:00Z")

        assertFailsWith<IllegalArgumentException> {
            createMaintenanceRecord(
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAtWithinSameMillisecond() {
        val createdAt = Instant.parse("2026-08-21T08:00:00.000000500Z")
        val updatedAt = Instant.parse("2026-08-21T08:00:00.000000400Z")

        assertFailsWith<IllegalArgumentException> {
            createMaintenanceRecord(
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    private fun createMaintenanceRecord(
        id: MaintenanceRecordId =
            MaintenanceRecordId("550e8400-e29b-41d4-a716-446655440000"),
        taskId: MaintenanceTaskId =
            MaintenanceTaskId("550e8400-e29b-41d4-a716-446655440001"),
        completedOn: LocalDate = LocalDate(2026, 8, 21),
        notes: String? = "Filtros lavados y secados",
        createdAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        updatedAt: Instant = createdAt,
    ): MaintenanceRecord {
        return MaintenanceRecord(
            id = id,
            taskId = taskId,
            completedOn = completedOn,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
