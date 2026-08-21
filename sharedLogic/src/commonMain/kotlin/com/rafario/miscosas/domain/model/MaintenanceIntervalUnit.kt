package com.rafario.miscosas.domain.model

enum class MaintenanceIntervalUnit(val code: String) {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year");

    companion object {
        fun fromCodeOrNull(code: String): MaintenanceIntervalUnit? =
            entries.firstOrNull { it.code == code }
    }
}
