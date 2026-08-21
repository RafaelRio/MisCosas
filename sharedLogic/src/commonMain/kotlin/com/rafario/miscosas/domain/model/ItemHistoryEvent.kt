package com.rafario.miscosas.domain.model

import kotlin.time.Instant

data class ItemHistoryEvent(
    val id: ItemHistoryEventId,
    val itemId: ItemId,
    val type: ItemHistoryEventType,
    val occurredAt: Instant,
    val recordedBy: UserId,
    val statusChange: ItemStatusChange?,
    val details: String?,
) {
    init {
        if (details != null) {
            require(details.isNotBlank()) {
                "ItemHistoryEvent details must not be blank"
            }
        }

        if (type == ItemHistoryEventType.STATUS_CHANGED) {
            require(statusChange != null) {
                "ItemHistoryEvent statusChange must not be null"
            }
        } else {
            require(statusChange == null) {
                "ItemHistoryEvent statusChange must be null"
            }
        }
    }
}