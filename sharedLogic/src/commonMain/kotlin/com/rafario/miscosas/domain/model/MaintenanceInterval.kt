package com.rafario.miscosas.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

data class MaintenanceInterval(
    val amount: Int,
    val unit: MaintenanceIntervalUnit,
) {
    init {
        require(amount > 0) {
            "MaintenanceInterval amount must be greater than zero"
        }
    }

    fun nextDateAfter(date: LocalDate): LocalDate {
        val dateTimeUnit = when (unit) {
            MaintenanceIntervalUnit.DAY -> DateTimeUnit.DAY
            MaintenanceIntervalUnit.WEEK -> DateTimeUnit.WEEK
            MaintenanceIntervalUnit.MONTH -> DateTimeUnit.MONTH
            MaintenanceIntervalUnit.YEAR -> DateTimeUnit.YEAR
        }

        return date.plus(amount, dateTimeUnit)
    }
}
