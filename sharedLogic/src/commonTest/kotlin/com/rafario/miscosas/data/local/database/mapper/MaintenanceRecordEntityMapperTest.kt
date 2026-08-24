package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.MaintenanceRecordEntity
import com.rafario.miscosas.domain.model.MaintenanceRecord
import com.rafario.miscosas.domain.model.MaintenanceRecordId
import com.rafario.miscosas.domain.model.MaintenanceTaskId
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class MaintenanceRecordEntityMapperTest {

    @Test
    fun mapsMaintenanceRecordToEntityPreservingAllFieldsAndNanoseconds() {
        val maintenanceRecord = MaintenanceRecord(
            id = MaintenanceRecordId("550e8400-e29b-41d4-a716-446655440000"),
            taskId = MaintenanceTaskId("550e5220-e29b-41d4-a716-446455440010"),
            completedOn = LocalDate(2026, 4, 20),
            notes = "Realizar chequeo general",
            createdAt = Instant.parse("2026-08-21T08:00:00.000000400Z"),
            updatedAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
        )
        val entity = maintenanceRecord.toEntity()

        assertEquals(maintenanceRecord.id.value, entity.id)
        assertEquals(maintenanceRecord.taskId.value, entity.taskId)
        assertEquals(
            maintenanceRecord.completedOn.toEpochDays(),
            entity.completedOnEpochDay,
        )
        assertEquals(maintenanceRecord.notes, entity.notes)
        assertEquals(maintenanceRecord.createdAt.epochSeconds, entity.createdAtEpochSeconds)
        assertEquals(maintenanceRecord.createdAt.nanosecondsOfSecond, entity.createdAtNanoseconds)
        assertEquals(maintenanceRecord.updatedAt.epochSeconds, entity.updatedAtEpochSeconds)
        assertEquals(maintenanceRecord.updatedAt.nanosecondsOfSecond, entity.updatedAtNanoseconds)
    }

    @Test
    fun mapsMaintenanceRecordEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val entity = createMaintenanceRecordEntity()
        val maintenanceRecord = entity.toDomain()

        assertEquals(MaintenanceRecordId(entity.id), maintenanceRecord.id)
        assertEquals(MaintenanceTaskId(entity.taskId), maintenanceRecord.taskId)
        assertEquals(
            LocalDate.fromEpochDays(entity.completedOnEpochDay),
            maintenanceRecord.completedOn,
        )
        assertEquals(entity.notes, maintenanceRecord.notes)
        assertEquals(
            Instant.fromEpochSeconds(
                epochSeconds = entity.createdAtEpochSeconds,
                nanosecondAdjustment = entity.createdAtNanoseconds.toLong(),
            ),
            maintenanceRecord.createdAt,
        )
        assertEquals(
            Instant.fromEpochSeconds(
                epochSeconds = entity.updatedAtEpochSeconds,
                nanosecondAdjustment = entity.updatedAtNanoseconds.toLong(),
            ),
            maintenanceRecord.updatedAt,
        )
    }

    @Test
    fun preservesNullNotesWhenAbsent() {
        val entity = createMaintenanceRecordEntity(
            notes = null,
        )

        val maintenanceRecord = entity.toDomain()

        assertNull(maintenanceRecord.notes)
    }

    @Test
    fun mapsNullNotesToEntityWhenAbsent() {
        val maintenanceRecord = MaintenanceRecord(
            id = MaintenanceRecordId("550e8400-e29b-41d4-a716-446655440000"),
            taskId = MaintenanceTaskId("550e5220-e29b-41d4-a716-446455440010"),
            completedOn = LocalDate(2026, 4, 20),
            notes = null,
            createdAt = Instant.parse("2026-08-21T08:00:00.000000400Z"),
            updatedAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
        )

        assertNull(maintenanceRecord.toEntity().notes)
    }

    @Test
    fun rejectsInvalidStoredCompletedOnEpochDay() {
        val entity = createMaintenanceRecordEntity(
            completedOnEpochDay = Long.MAX_VALUE,
        )

        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val entity = createMaintenanceRecordEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(createdAtNanoseconds = -1).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(updatedAtNanoseconds = 1_000_000_000).toDomain()
        }
    }

    private fun createMaintenanceRecordEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440000",
        taskId: String = "550e5220-e29b-41d4-a716-446455440010",
        completedOnEpochDay: Long = LocalDate(2026, 4, 20).toEpochDays(),
        notes: String? = "Realizar chequeo general",
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 400,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 500,
    ): MaintenanceRecordEntity = MaintenanceRecordEntity(
        id = id,
        taskId = taskId,
        completedOnEpochDay = completedOnEpochDay,
        notes = notes,
        createdAtEpochSeconds = createdAtEpochSeconds,
        createdAtNanoseconds = createdAtNanoseconds,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
        updatedAtNanoseconds = updatedAtNanoseconds,
    )
}
