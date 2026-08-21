package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class MaintenanceTaskTest {

    @Test
    fun keepsMaintenanceTaskConfiguration() {
        val id =
            MaintenanceTaskId("550e8400-e29b-41d4-a716-446655440000")
        val itemId =
            ItemId("550e8400-e29b-41d4-a716-446655440001")
        val name = "Limpiar filtros"
        val details = "Retirar y lavar los filtros"
        val interval = MaintenanceInterval(
            amount = 3,
            unit = MaintenanceIntervalUnit.MONTH,
        )
        val firstDueDate = LocalDate(2026, 11, 21)
        val reminderDaysBeforeDue = 7
        val notes = "Usar agua templada"
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00Z")

        val maintenanceTask = createMaintenanceTask(
            id = id,
            itemId = itemId,
            name = name,
            details = details,
            interval = interval,
            firstDueDate = firstDueDate,
            reminderDaysBeforeDue = reminderDaysBeforeDue,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(id, maintenanceTask.id)
        assertEquals(itemId, maintenanceTask.itemId)
        assertEquals(name, maintenanceTask.name)
        assertEquals(details, maintenanceTask.details)
        assertEquals(interval, maintenanceTask.interval)
        assertEquals(firstDueDate, maintenanceTask.firstDueDate)
        assertEquals(reminderDaysBeforeDue, maintenanceTask.reminderDaysBeforeDue)
        assertEquals(notes, maintenanceTask.notes)
        assertEquals(createdAt, maintenanceTask.createdAt)
        assertEquals(updatedAt, maintenanceTask.updatedAt)
    }

    @Test
    fun allowsMissingOptionalConfiguration() {
        val task = createMaintenanceTask(
            details = null,
            reminderDaysBeforeDue = null,
            notes = null,
        )

        assertNull(task.details)
        assertNull(task.reminderDaysBeforeDue)
        assertNull(task.notes)
    }

    @Test
    fun rejectsBlankName() {
        assertFailsWith<IllegalArgumentException> {
            createMaintenanceTask(name = "    ")
        }
    }

    @Test
    fun rejectsBlankDetailsWhenPresent() {
        assertFailsWith<IllegalArgumentException> {
            createMaintenanceTask(details = "    ")
        }
    }

    @Test
    fun rejectsBlankNotesWhenPresent() {
        assertFailsWith<IllegalArgumentException> {
            createMaintenanceTask(notes = "    ")
        }
    }

    @Test
    fun rejectsNegativeReminderDaysBeforeDue() {
        assertFailsWith<IllegalArgumentException> {
            createMaintenanceTask(reminderDaysBeforeDue = -1)
        }
    }

    @Test
    fun allowsReminderOnDueDate() {
        val task = createMaintenanceTask(reminderDaysBeforeDue = 0)

        assertEquals(0, task.reminderDaysBeforeDue)
    }

    @Test
    fun allowsUpdatedAtEqualToCreatedAt() {
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T08:00:00Z")
        val maintenanceTask = createMaintenanceTask(
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(createdAt, maintenanceTask.createdAt)
        assertEquals(updatedAt, maintenanceTask.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-20T08:00:00Z")
        assertFailsWith<IllegalArgumentException> {
            createMaintenanceTask(
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
            createMaintenanceTask(
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    @Test
    fun returnsFirstDueDateWhenNeverCompleted() {
        val firstDueDate = LocalDate(2026, 11, 21)
        val task = createMaintenanceTask(
            firstDueDate = firstDueDate,
        )

        val nextDueDate = task.nextDueDate(
            lastCompletedOn = null,
        )

        assertEquals(firstDueDate, nextDueDate)
    }

    @Test
    fun calculatesNextDueDateFromLastCompletion() {
        val task = createMaintenanceTask(
            interval = MaintenanceInterval(
                amount = 3,
                unit = MaintenanceIntervalUnit.MONTH,
            ),
            firstDueDate = LocalDate(2026, 11, 21),
        )
        val lastCompletedOn = LocalDate(2026, 11, 25)

        val nextDueDate = task.nextDueDate(
            lastCompletedOn = lastCompletedOn,
        )

        assertEquals(
            LocalDate(2027, 2, 25),
            nextDueDate,
        )
    }

    private fun createMaintenanceTask(
        id: MaintenanceTaskId = MaintenanceTaskId("550e8400-e29b-41d4-a716-446655440000"),
        itemId: ItemId = ItemId("550e8400-e29b-41d4-a716-446655440001"),
        name: String = "Limpiar filtros",
        details: String? = "Retirar y lavar los filtros",
        interval: MaintenanceInterval = MaintenanceInterval(
            amount = 3,
            unit = MaintenanceIntervalUnit.MONTH,
        ),
        firstDueDate: LocalDate = LocalDate(2026, 11, 21),
        reminderDaysBeforeDue: Int? = 7,
        notes: String? = "Usar agua templada",
        createdAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        updatedAt: Instant = createdAt,
    ): MaintenanceTask {
        return MaintenanceTask(
            id = id,
            itemId = itemId,
            name = name,
            details = details,
            interval = interval,
            firstDueDate = firstDueDate,
            reminderDaysBeforeDue = reminderDaysBeforeDue,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
