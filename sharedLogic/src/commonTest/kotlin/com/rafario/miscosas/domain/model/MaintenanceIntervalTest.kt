package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MaintenanceIntervalTest {

    @Test
    fun keepsAmountAndUnit() {
        val interval = MaintenanceInterval(
            amount = 3,
            unit = MaintenanceIntervalUnit.MONTH,
        )

        assertEquals(3, interval.amount)
        assertEquals(MaintenanceIntervalUnit.MONTH, interval.unit)
    }

    @Test
    fun rejectsZeroAmount() {
        assertFailsWith<IllegalArgumentException> {
            MaintenanceInterval(
                amount = 0,
                unit = MaintenanceIntervalUnit.MONTH,
            )
        }
    }

    @Test
    fun rejectsNegativeAmount() {
        assertFailsWith<IllegalArgumentException> {
            MaintenanceInterval(
                amount = -1,
                unit = MaintenanceIntervalUnit.MONTH,
            )
        }
    }

    @Test
    fun calculatesNextDateAfterMonthlyInterval() {
        val interval = MaintenanceInterval(
            amount = 3,
            unit = MaintenanceIntervalUnit.MONTH,
        )
        val date = LocalDate(2026, 1, 15)

        val nextDate = interval.nextDateAfter(date)

        assertEquals(
            LocalDate(2026, 4, 15),
            nextDate,
        )
    }

    @Test
    fun addsDaysAcrossYearBoundary() {
        val interval = MaintenanceInterval(
            amount = 2,
            unit = MaintenanceIntervalUnit.DAY,
        )
        val date = LocalDate(2026, 12, 30)
        val nextDate = interval.nextDateAfter(date)
        assertEquals(
            LocalDate(2027, 1, 1),
            nextDate,
        )
    }

    @Test
    fun addsWeeksAcrossYearBoundary() {
        val interval = MaintenanceInterval(
            amount = 2,
            unit = MaintenanceIntervalUnit.WEEK,
        )
        val date = LocalDate(2026, 12, 25)
        val nextDate = interval.nextDateAfter(date)
        assertEquals(
            LocalDate(2027, 1, 8),
            nextDate,
        )
    }

    @Test
    fun adjustsMonthlyIntervalToLastDayOfFebruary() {
        val interval = MaintenanceInterval(
            amount = 1,
            unit = MaintenanceIntervalUnit.MONTH,
        )
        val date = LocalDate(2026, 1, 31)
        val nextDate = interval.nextDateAfter(date)
        assertEquals(
            LocalDate(2026, 2, 28),
            nextDate,
        )
    }

    @Test
    fun adjustsMonthlyIntervalToLeapDay() {
        val interval = MaintenanceInterval(
            amount = 1,
            unit = MaintenanceIntervalUnit.MONTH,
        )
        val date = LocalDate(2028, 1, 31)
        val nextDate = interval.nextDateAfter(date)
        assertEquals(
            LocalDate(2028, 2, 29),
            nextDate,
        )
    }

    @Test
    fun adjustsMonthlyIntervalToLastDayOfApril() {
        val interval = MaintenanceInterval(
            amount = 1,
            unit = MaintenanceIntervalUnit.MONTH,
        )
        val date = LocalDate(2026, 3, 31)
        val nextDate = interval.nextDateAfter(date)
        assertEquals(
            LocalDate(2026, 4, 30),
            nextDate,
        )
    }

    @Test
    fun doesNotPreserveEndOfMonth() {
        val interval = MaintenanceInterval(
            amount = 1,
            unit = MaintenanceIntervalUnit.MONTH,
        )
        val date = LocalDate(2026, 2, 28)
        val nextDate = interval.nextDateAfter(date)
        assertEquals(
            LocalDate(2026, 3, 28),
            nextDate,
        )
    }

    @Test
    fun adjustsYearlyIntervalFromLeapDay() {
        val interval = MaintenanceInterval(
            amount = 1,
            unit = MaintenanceIntervalUnit.YEAR,
        )
        val date = LocalDate(2028, 2, 29)
        val nextDate = interval.nextDateAfter(date)
        assertEquals(
            LocalDate(2029, 2, 28),
            nextDate,
        )
    }
}
