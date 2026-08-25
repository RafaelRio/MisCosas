package com.rafario.miscosas.data.sync.model

internal enum class SyncOperation(val code: String) {
    UPSERT(code = "upsert"),
    DELETE(code = "delete");

    companion object {
        fun fromCodeOrNull(code: String): SyncOperation? {
            return entries.find { it.code == code }
        }
    }
}
