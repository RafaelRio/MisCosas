package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.MaintenanceTaskEntity
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.MaintenanceInterval
import com.rafario.miscosas.domain.model.MaintenanceIntervalUnit
import com.rafario.miscosas.domain.model.MaintenanceTask
import com.rafario.miscosas.domain.model.MaintenanceTaskId
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class MaintenanceTaskEntityMapperTest {

    @Test
    fun mapsMaintenanceTaskToEntityPreservingAllFieldsAndNanoseconds() {
        val maintenanceTask = MaintenanceTask(
            id = MaintenanceTaskId("550e8400-e29b-41d4-a716-446655440000"),
            itemId = ItemId("550e5200-e29b-41d4-a716-446455440010"),
            name = "Mantenimiento preventivo",
            details = "Realizar chequeo general",
            interval = MaintenanceInterval(
                amount = 3,
                unit = MaintenanceIntervalUnit.MONTH,
            ),
            firstDueDate = LocalDate(2026, 4, 20),
            reminderDaysBeforeDue = 15,
            notes = "Realizar chequeo general",
            createdAt = Instant.parse("2026-08-21T08:00:00.000000400Z"),
            updatedAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
        )
        val entity = maintenanceTask.toEntity()

        assertEquals(maintenanceTask.id.value, entity.id)
        assertEquals(maintenanceTask.itemId.value, entity.itemId)
        assertEquals(maintenanceTask.name, entity.name)
        assertEquals(maintenanceTask.details, entity.details)
        assertEquals(maintenanceTask.interval.amount, entity.intervalAmount)
        assertEquals(maintenanceTask.interval.unit.code, entity.intervalUnitCode)
        assertEquals(
            maintenanceTask.firstDueDate.toEpochDays(),
            entity.firstDueDateEpochDay,
        )
        assertEquals(
            maintenanceTask.reminderDaysBeforeDue,
            entity.reminderDaysBeforeDue,
        )
        assertEquals(maintenanceTask.notes, entity.notes)
        assertEquals(maintenanceTask.createdAt.epochSeconds, entity.createdAtEpochSeconds)
        assertEquals(maintenanceTask.createdAt.nanosecondsOfSecond, entity.createdAtNanoseconds)
        assertEquals(maintenanceTask.updatedAt.epochSeconds, entity.updatedAtEpochSeconds)
        assertEquals(maintenanceTask.updatedAt.nanosecondsOfSecond, entity.updatedAtNanoseconds)
    }

    @Test
    fun mapsMaintenanceTaskEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val entity = createMaintenanceTaskEntity()
        val maintenanceTask = entity.toDomain()

        assertEquals(MaintenanceTaskId(entity.id), maintenanceTask.id)
        assertEquals(ItemId(entity.itemId), maintenanceTask.itemId)
        assertEquals(entity.name, maintenanceTask.name)
        assertEquals(entity.details, maintenanceTask.details)
        assertEquals(
            MaintenanceInterval(
                amount = 3,
                unit = MaintenanceIntervalUnit.MONTH,
            ),
            maintenanceTask.interval,
        )
        assertEquals(
            LocalDate.fromEpochDays(entity.firstDueDateEpochDay),
            maintenanceTask.firstDueDate,
        )
        assertEquals(entity.reminderDaysBeforeDue, maintenanceTask.reminderDaysBeforeDue)
        assertEquals(entity.notes, maintenanceTask.notes)
        assertEquals(
            Instant.fromEpochSeconds(
                epochSeconds = entity.createdAtEpochSeconds,
                nanosecondAdjustment = entity.createdAtNanoseconds.toLong(),
            ),
            maintenanceTask.createdAt,
        )
        assertEquals(
            Instant.fromEpochSeconds(
                epochSeconds = entity.updatedAtEpochSeconds,
                nanosecondAdjustment = entity.updatedAtNanoseconds.toLong(),
            ),
            maintenanceTask.updatedAt,
        )
    }

    @Test
    fun rejectsUnknownStoredIntervalUnitCode() {
        val entity = createMaintenanceTaskEntity(
            intervalUnitCode = "unknown",
        )

        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsNonPositiveStoredIntervalAmount() {
        val entity = createMaintenanceTaskEntity(
            intervalAmount = 0,
        )

        assertFailsWith<IllegalArgumentException> {
            entity.toDomain()
        }
    }

    @Test
    fun preservesNullableFieldsWhenAbsent() {
        val entity = createMaintenanceTaskEntity(
            details = null,
            reminderDaysBeforeDue = null,
            notes = null,
        )

        val maintenanceTask = entity.toDomain()

        assertNull(maintenanceTask.details)
        assertNull(maintenanceTask.reminderDaysBeforeDue)
        assertNull(maintenanceTask.notes)
    }

    @Test
    fun rejectsInvalidStoredFirstDueDateEpochDay() {
        val entity = createMaintenanceTaskEntity(
            firstDueDateEpochDay = Long.MAX_VALUE,
        )

        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val entity = createMaintenanceTaskEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(createdAtNanoseconds = -1).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(updatedAtNanoseconds = 1_000_000_000).toDomain()
        }
    }

    private fun createMaintenanceTaskEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440000",
        itemId: String = "550e5200-e29b-41d4-a716-446455440010",
        name: String = "Mantenimiento preventivo",
        details: String? = "Realizar chequeo general",
        intervalAmount: Int = 3,
        intervalUnitCode: String = MaintenanceIntervalUnit.MONTH.code,
        firstDueDateEpochDay: Long = LocalDate(2026, 4, 20).toEpochDays(),
        reminderDaysBeforeDue: Int? = 15,
        notes: String? = "Revisar todos los componentes",
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 400,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 500,
    ): MaintenanceTaskEntity = MaintenanceTaskEntity(
        id = id,
        itemId = itemId,
        name = name,
        details = details,
        intervalAmount = intervalAmount,
        intervalUnitCode = intervalUnitCode,
        firstDueDateEpochDay = firstDueDateEpochDay,
        reminderDaysBeforeDue = reminderDaysBeforeDue,
        notes = notes,
        createdAtEpochSeconds = createdAtEpochSeconds,
        createdAtNanoseconds = createdAtNanoseconds,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
        updatedAtNanoseconds = updatedAtNanoseconds,
    )
}
