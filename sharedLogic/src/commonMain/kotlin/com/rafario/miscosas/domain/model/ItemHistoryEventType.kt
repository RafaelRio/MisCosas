package com.rafario.miscosas.domain.model

enum class ItemHistoryEventType(
    val code: String,
) {
    CREATED("created"),
    PURCHASE_RECORDED("purchase_recorded"),
    WARRANTY_ADDED("warranty_added"),
    DOCUMENT_ADDED("document_added"),
    MAINTENANCE_COMPLETED("maintenance_completed"),
    REPAIR_RECORDED("repair_recorded"),
    STATUS_CHANGED("status_changed"),
    ARCHIVED("archived"),
    UNARCHIVED("unarchived");

    companion object {
        fun fromCodeOrNull(code: String): ItemHistoryEventType? {
            return entries.find { it.code == code }
        }
    }
}