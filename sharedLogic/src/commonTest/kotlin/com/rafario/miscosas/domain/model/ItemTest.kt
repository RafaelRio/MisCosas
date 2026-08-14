package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Instant

class ItemTest {

    @Test
    fun keepsCoreItemInformation() {
        val createdAt = Instant.parse("2026-08-14T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-14T09:00:00Z")

        val item = createItem(
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals("MacBook Pro", item.name)
        assertEquals(BuiltInCategory.TECHNOLOGY.id, item.categoryId)
        assertEquals(ItemStatus.ACTIVE, item.status)
        assertEquals(createdAt, item.createdAt)
        assertEquals(updatedAt, item.updatedAt)
    }

    @Test
    fun rejectsBlankItemName() {
        assertFailsWith<IllegalArgumentException> {
            createItem(name = "   ")
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        assertFailsWith<IllegalArgumentException> {
            createItem(
                createdAt = Instant.parse("2026-08-14T09:00:00Z"),
                updatedAt = Instant.parse("2026-08-14T08:00:00Z"),
            )
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAtWithinSameMillisecond() {
        assertFailsWith<IllegalArgumentException> {
            createItem(
                createdAt = Instant.parse(
                    "2026-08-14T08:00:00.000000500Z"
                ),
                updatedAt = Instant.parse(
                    "2026-08-14T08:00:00.000000400Z"
                ),
            )
        }
    }

    @Test
    fun keepsOptionalItemInformation() {
        val purchase = Purchase(
            date = LocalDate(2025, 3, 15),
            price = Money(
                minorUnits = 89_999,
                currency = CurrencyCode.EUR,
            ),
            seller = "PcComponentes",
        )

        val item = createItem(
            brand = "Apple",
            model = "MacBook Pro M4",
            serialNumber = "C02ABC123XYZ",
            purchase = purchase,
        )

        assertEquals("Apple", item.brand)
        assertEquals("MacBook Pro M4", item.model)
        assertEquals("C02ABC123XYZ", item.serialNumber)
        assertEquals(purchase, item.purchase)
    }

    @Test
    fun rejectsBlankBrand() {
        assertFailsWith<IllegalArgumentException> {
            createItem(brand = "   ")
        }
    }

    @Test
    fun rejectsBlankModel() {
        assertFailsWith<IllegalArgumentException> {
            createItem(model = "   ")
        }
    }

    @Test
    fun rejectsBlankSerialNumber() {
        assertFailsWith<IllegalArgumentException> {
            createItem(serialNumber = "   ")
        }
    }

    @Test
    fun keepsFavoriteAndArchiveIndependentlyFromStatus() {
        val item = createItem(
            status = ItemStatus.SOLD,
            isFavorite = true,
            isArchived = true,
        )

        assertEquals(ItemStatus.SOLD, item.status)
        assertTrue(item.isFavorite)
        assertTrue(item.isArchived)
    }

    private fun createItem(
        name: String = "MacBook Pro",
        createdAt: Instant = Instant.parse("2026-08-14T08:00:00Z"),
        updatedAt: Instant = createdAt,
        brand: String? = null,
        model: String? = null,
        serialNumber: String? = null,
        purchase: Purchase? = null,
        status: ItemStatus = ItemStatus.ACTIVE,
        isFavorite: Boolean = false,
        isArchived: Boolean = false,
    ): Item =
        Item(
            id = ItemId("550e8400-e29b-41d4-a716-446655440000"),
            householdId = HouseholdId(
                "7c9e6679-7425-40de-944b-e07fc1f90ae7"
            ),
            name = name,
            categoryId = BuiltInCategory.TECHNOLOGY.id,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            brand = brand,
            model = model,
            serialNumber = serialNumber,
            purchase = purchase,
            isFavorite = isFavorite,
            isArchived = isArchived,
        )
}
