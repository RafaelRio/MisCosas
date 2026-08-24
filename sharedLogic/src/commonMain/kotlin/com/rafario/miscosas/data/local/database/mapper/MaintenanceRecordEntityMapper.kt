package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.MaintenanceRecordEntity
import com.rafario.miscosas.domain.model.MaintenanceRecord
import com.rafario.miscosas.domain.model.MaintenanceRecordId
import com.rafario.miscosas.domain.model.MaintenanceTaskId

internal fun MaintenanceRecord.toEntity(): MaintenanceRecordEntity = MaintenanceRecordEntity(
    id = id.value,
    taskId = taskId.value,
    completedOnEpochDay = completedOn.toEpochDays(),
    notes = notes,
    createdAtEpochSeconds = createdAt.epochSeconds,
    createdAtNanoseconds = createdAt.nanosecondsOfSecond,
    updatedAtEpochSeconds = updatedAt.epochSeconds,
    updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
)

internal fun MaintenanceRecordEntity.toDomain(): MaintenanceRecord = MaintenanceRecord(
    id = MaintenanceRecordId(id),
    taskId = MaintenanceTaskId(taskId),
    completedOn = localDateFromEpochDay(
        epochDay = completedOnEpochDay,
        fieldName = "MaintenanceRecordEntity.completedOn",
    ),
    notes = notes,
    createdAt = instantFromEpochColumns(
        epochSeconds = createdAtEpochSeconds,
        nanosecondsOfSecond = createdAtNanoseconds,
        fieldName = "MaintenanceRecordEntity.createdAt",
    ),
    updatedAt = instantFromEpochColumns(
        epochSeconds = updatedAtEpochSeconds,
        nanosecondsOfSecond = updatedAtNanoseconds,
        fieldName = "MaintenanceRecordEntity.updatedAt",
    ),
)