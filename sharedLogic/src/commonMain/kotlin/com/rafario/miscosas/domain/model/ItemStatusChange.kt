package com.rafario.miscosas.domain.model

data class ItemStatusChange(
    val previousStatus: ItemStatus,
    val newStatus: ItemStatus,
) {
    init {
        require(previousStatus != newStatus) {
            "ItemStatusChange statuses must be different"
        }
    }
}
