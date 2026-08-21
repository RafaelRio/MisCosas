package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MaintenanceIntervalUnitTest {

    @Test
    fun exposesStableCodes() {
        assertEquals("day", MaintenanceIntervalUnit.DAY.code)
        assertEquals("week", MaintenanceIntervalUnit.WEEK.code)
        assertEquals("month", MaintenanceIntervalUnit.MONTH.code)
        assertEquals("year", MaintenanceIntervalUnit.YEAR.code)
    }

    @Test
    fun findsUnitByCode() {
        assertEquals(
            MaintenanceIntervalUnit.DAY,
            MaintenanceIntervalUnit.fromCodeOrNull("day"),
        )
        assertEquals(
            MaintenanceIntervalUnit.WEEK,
            MaintenanceIntervalUnit.fromCodeOrNull("week"),
        )
        assertEquals(
            MaintenanceIntervalUnit.MONTH,
            MaintenanceIntervalUnit.fromCodeOrNull("month"),
        )
        assertEquals(
            MaintenanceIntervalUnit.YEAR,
            MaintenanceIntervalUnit.fromCodeOrNull("year"),
        )
    }

    @Test
    fun returnsNullForUnknownCode() {
        assertNull(
            MaintenanceIntervalUnit.fromCodeOrNull("unknown"),
        )
    }
}
