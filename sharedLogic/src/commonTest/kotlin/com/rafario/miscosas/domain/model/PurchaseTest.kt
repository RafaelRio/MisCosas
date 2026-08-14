package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PurchaseTest {

    @Test
    fun keepsProvidedPurchaseInformation() {
        val date = LocalDate(2025, 3, 15)
        val price = Money(
            minorUnits = 89_999,
            currency = CurrencyCode.EUR,
        )

        val purchase = Purchase(
            date = date,
            price = price,
            seller = "PcComponentes",
        )

        assertEquals(date, purchase.date)
        assertEquals(price, purchase.price)
        assertEquals("PcComponentes", purchase.seller)
    }

    @Test
    fun rejectsPurchaseWithoutInformation() {
        assertFailsWith<IllegalArgumentException> {
            Purchase(
                date = null,
                price = null,
                seller = null,
            )
        }
    }

    @Test
    fun rejectsBlankSeller() {
        assertFailsWith<IllegalArgumentException> {
            Purchase(
                date = LocalDate(2025, 3, 15),
                price = null,
                seller = "   ",
            )
        }
    }

    @Test
    fun acceptsPurchaseWithOnlyDate() {
        val date = LocalDate(2025, 3, 15)

        val purchase = Purchase(
            date = date,
            price = null,
            seller = null,
        )

        assertEquals(date, purchase.date)
        assertNull(purchase.price)
        assertNull(purchase.seller)
    }

    @Test
    fun acceptsPurchaseWithOnlyPrice() {
        val price = Money(
            minorUnits = 89_999,
            currency = CurrencyCode.EUR,
        )

        val purchase = Purchase(
            date = null,
            price = price,
            seller = null,
        )

        assertNull(purchase.date)
        assertEquals(price, purchase.price)
        assertNull(purchase.seller)
    }

    @Test
    fun acceptsPurchaseWithOnlySeller() {
        val purchase = Purchase(
            date = null,
            price = null,
            seller = "Wallapop",
        )

        assertNull(purchase.date)
        assertNull(purchase.price)
        assertEquals("Wallapop", purchase.seller)
    }
}
