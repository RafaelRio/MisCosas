package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class MoneyTest {

    @Test
    fun preservesExactMinorUnits() {
        val money = Money(
            minorUnits = 1_099L,
            currency = CurrencyCode.EUR
        )

        assertEquals(1_099L, money.minorUnits)
        assertEquals(CurrencyCode.EUR, money.currency)
    }

    @Test
    fun rejectsNegativeAmounts() {
        assertFailsWith<IllegalArgumentException> {
            Money(
                minorUnits = -1L,
                currency = CurrencyCode.EUR,
            )
        }
    }

    @Test
    fun acceptsZero() {
        val money = Money(
            minorUnits = 0L,
            currency = CurrencyCode.EUR,
        )

        assertEquals(0L, money.minorUnits)
    }

    @Test
    fun currencyIsPartOfEquality() {
        val euros = Money(
            minorUnits = 1_099L,
            currency = CurrencyCode.EUR,
        )
        val dollars = Money(
            minorUnits = 1_099L,
            currency = CurrencyCode("USD"),
        )

        assertNotEquals(euros, dollars)
    }
}