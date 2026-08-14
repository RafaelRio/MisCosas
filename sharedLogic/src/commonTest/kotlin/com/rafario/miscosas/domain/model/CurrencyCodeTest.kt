package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CurrencyCodeTest {
    @Test
    fun acceptsValidCurrencyCodes() {
        val euros = CurrencyCode("EUR")
        val dollars = CurrencyCode("USD")

        assertEquals("EUR", euros.value)
        assertEquals("USD", dollars.value)
    }

    @Test
    fun rejectsInvalidCurrencyCodes() {
        val invalidCodes = listOf(
            "eur",
            "EU",
            "EURO",
            "EU1",
            " EUR",
            "EÜR",
        )

        invalidCodes.forEach { invalidCode ->
            assertFailsWith<IllegalArgumentException> {
                CurrencyCode(invalidCode)
            }
        }
    }

    @Test
    fun exposesEuroAsConvenienceValue() {
        assertEquals(
            CurrencyCode("EUR"),
            CurrencyCode.EUR,
        )
    }
}