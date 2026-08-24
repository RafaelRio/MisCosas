package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.MaintenanceTaskEntity
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.MaintenanceInterval
import com.rafario.miscosas.domain.model.MaintenanceIntervalUnit
import com.rafario.miscosas.domain.model.MaintenanceTask
import com.rafario.miscosas.domain.model.MaintenanceTaskId

internal fun MaintenanceTask.toEntity(): MaintenanceTaskEntity {
    return MaintenanceTaskEntity(
        id = id.value,
        itemId = itemId.value,
        name = name,
        details = details,
        intervalAmount = interval.amount,
        intervalUnitCode = interval.unit.code,
        firstDueDateEpochDay = firstDueDate.toEpochDays(),
        reminderDaysBeforeDue = reminderDaysBeforeDue,
        notes = notes,
        createdAtEpochSeconds = createdAt.epochSeconds,
        createdAtNanoseconds = createdAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
    )
}

internal fun MaintenanceTaskEntity.toDomain(): MaintenanceTask {
    val intervalUnit = checkNotNull(MaintenanceIntervalUnit.fromCodeOrNull(intervalUnitCode)) {
        "Invalid interval unit code: $intervalUnitCode"
    }

    return MaintenanceTask(
        id = MaintenanceTaskId(id),
        itemId = ItemId(itemId),
        name = name,
        details = details,
        interval = MaintenanceInterval(
            amount = intervalAmount,
            unit = intervalUnit,
        ),
        firstDueDate = localDateFromEpochDay(
            epochDay = firstDueDateEpochDay,
            fieldName = "MaintenanceTaskEntity.firstDueDate",
        ),
        reminderDaysBeforeDue = reminderDaysBeforeDue,
        notes = notes,
        createdAt = instantFromEpochColumns(
            epochSeconds = createdAtEpochSeconds,
            nanosecondsOfSecond = createdAtNanoseconds,
            fieldName = "MaintenanceTaskEntity.createdAt",
        ),
        updatedAt = instantFromEpochColumns(
            epochSeconds = updatedAtEpochSeconds,
            nanosecondsOfSecond = updatedAtNanoseconds,
            fieldName = "MaintenanceTaskEntity.updatedAt",
        ),
    )
}
