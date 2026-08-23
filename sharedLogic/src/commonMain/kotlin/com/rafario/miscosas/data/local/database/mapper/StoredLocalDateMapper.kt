package com.rafario.miscosas.data.local.database.mapper

import kotlinx.datetime.LocalDate

internal fun localDateFromEpochDay(
    epochDay: Long,
    fieldName: String,
): LocalDate {
    try {
        return LocalDate.fromEpochDays(epochDay)
    } catch (cause: IllegalArgumentException) {
        throw IllegalStateException(
            "$fieldName: $epochDay is not a valid epoch day",
            cause,
        )
    }
}