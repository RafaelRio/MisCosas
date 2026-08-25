package com.rafario.miscosas.data.sync.model

internal enum class SyncRecordType(val code: String) {
    USER(code = "user"),
    HOUSEHOLD(code = "household"),
    HOUSEHOLD_MEMBER(code = "household_member"),
    ITEM(code = "item"),
    DOCUMENT(code = "document"),
    WARRANTY(code = "warranty"),
    RETURN_PERIOD(code = "return_period"),
    MAINTENANCE_TASK(code = "maintenance_task"),
    MAINTENANCE_RECORD(code = "maintenance_record"),
    ITEM_HISTORY_EVENT(code = "item_history_event");

    companion object {
        fun fromCodeOrNull(code: String) = entries.find { it.code == code }
    }
}