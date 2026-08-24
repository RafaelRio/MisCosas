package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.ItemHistoryEventEntity
import com.rafario.miscosas.domain.model.ItemHistoryEvent
import com.rafario.miscosas.domain.model.ItemHistoryEventId
import com.rafario.miscosas.domain.model.ItemHistoryEventType
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.ItemStatus
import com.rafario.miscosas.domain.model.ItemStatusChange
import com.rafario.miscosas.domain.model.UserId

internal fun ItemHistoryEvent.toEntity(): ItemHistoryEventEntity = ItemHistoryEventEntity(
    id = id.value,
    itemId = itemId.value,
    typeCode = type.code,
    occurredAtEpochSeconds = occurredAt.epochSeconds,
    occurredAtNanoseconds = occurredAt.nanosecondsOfSecond,
    recordedByUserId = recordedBy.value,
    previousStatusCode = statusChange?.previousStatus?.code,
    newStatusCode = statusChange?.newStatus?.code,
    details = details,
)

internal fun ItemHistoryEventEntity.toDomain(): ItemHistoryEvent {
    val eventType = checkNotNull(ItemHistoryEventType.fromCodeOrNull(typeCode)) {
        "Unknown ItemHistoryEventType code in ItemHistoryEventEntity: $typeCode"
    }
    val statusChange = toStatusChangeOrNull()

    check((eventType == ItemHistoryEventType.STATUS_CHANGED) == (statusChange != null)) {
        "ItemHistoryEventEntity status codes do not match event type: $typeCode"
    }

    return ItemHistoryEvent(
        id = ItemHistoryEventId(id),
        itemId = ItemId(itemId),
        type = eventType,
        occurredAt = instantFromEpochColumns(
            epochSeconds = occurredAtEpochSeconds,
            nanosecondsOfSecond = occurredAtNanoseconds,
            fieldName = "ItemHistoryEventEntity.occurredAt",
        ),
        recordedBy = UserId(recordedByUserId),
        statusChange = statusChange,
        details = details,
    )
}

private fun ItemHistoryEventEntity.toStatusChangeOrNull(): ItemStatusChange? {
    val previousCode = previousStatusCode
    val newCode = newStatusCode

    check((previousCode == null) == (newCode == null)) {
        "ItemHistoryEventEntity status codes must both be null or both be present"
    }

    if (previousCode == null) return null

    val previousStatus = checkNotNull(ItemStatus.fromCodeOrNull(previousCode)) {
        "Unknown previous ItemStatus code in ItemHistoryEventEntity: $previousCode"
    }
    val presentNewCode = checkNotNull(newCode) {
        "ItemHistoryEventEntity new status code must be present"
    }
    val newStatus = checkNotNull(ItemStatus.fromCodeOrNull(presentNewCode)) {
        "Unknown new ItemStatus code in ItemHistoryEventEntity: $presentNewCode"
    }

    return ItemStatusChange(
        previousStatus = previousStatus,
        newStatus = newStatus,
    )
}
