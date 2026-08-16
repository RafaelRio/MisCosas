package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class WarrantyTest {

    @Test
    fun keepsIdentityAndKnownDates() {
        val warrantyId = WarrantyId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )
        val itemId = ItemId(
            value = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
        )
        val startDate = LocalDate(2025, 3, 15)
        val endDate = LocalDate(2028, 3, 15)

        val warranty = createWarranty(
            id = warrantyId,
            itemId = itemId,
            startDate = startDate,
            endDate = endDate,
        )

        assertEquals(warrantyId, warranty.id)
        assertEquals(itemId, warranty.itemId)
        assertEquals(startDate, warranty.startDate)
        assertEquals(endDate, warranty.endDate)
    }

    @Test
    fun allowsUnknownEndDate() {
        val endDate = null
        val warranty = createWarranty(endDate = endDate)

        assertNull(warranty.endDate)
    }

    @Test
    fun allowsUnknownStartDate() {
        val startDate = null
        val warranty = createWarranty(startDate = startDate)

        assertNull(warranty.startDate)
    }

    @Test
    fun rejectsEndDateBeforeStartDate() {
        val warrantyId = WarrantyId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )
        val itemId = ItemId(
            value = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
        )
        val startDate = LocalDate(2025, 3, 15)
        val endDate = LocalDate(2025, 3, 10)

        assertFailsWith<IllegalArgumentException> {
            createWarranty(
                id = warrantyId,
                itemId = itemId,
                startDate = startDate,
                endDate = endDate
            )
        }
    }

    @Test
    fun allowsSameStartAndEndDate() {
        val date = LocalDate(2025, 3, 15)

        val warranty = createWarranty(
            startDate = date,
            endDate = date,
        )

        assertEquals(date, warranty.startDate)
        assertEquals(date, warranty.endDate)
    }

    @Test
    fun rejectsBlankProviderName() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(providerName = "   ")
        }
    }

    @Test
    fun rejectsBlankNotes() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(notes = "   ")
        }
    }

    @Test
    fun rejectsWarrantyWithoutInformation() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(
                startDate = null,
                endDate = null,
                type = null,
                providerName = null,
                notes = null
            )
        }
    }

    @Test
    fun keepsWarrantyInformation() {
        val type = WarrantyType.COMMERCIAL
        val providerName = "Apple"
        val notes = "Cobertura adicional"

        val warranty = createWarranty(type = type, providerName = providerName, notes = notes)

        assertEquals(type, warranty.type)
        assertEquals(providerName, warranty.providerName)
        assertEquals(notes, warranty.notes)
    }

    @Test
    fun allowsWarrantyWithOnlyType() {
        val warranty = createWarranty(
            startDate = null,
            endDate = null,
            type = WarrantyType.LEGAL,
            providerName = null,
            notes = null,
        )

        assertEquals(WarrantyType.LEGAL, warranty.type)
    }

    @Test
    fun keepsReminderDaysBeforeEnd() {
        val warranty = createWarranty(
            reminderDaysBeforeEnd = 30,
        )

        assertEquals(30, warranty.reminderDaysBeforeEnd)
    }

    @Test
    fun allowsReminderOnEndDate() {
        val warranty = createWarranty(
            reminderDaysBeforeEnd = 0,
        )

        assertEquals(0, warranty.reminderDaysBeforeEnd)
    }

    @Test
    fun rejectsNegativeReminderDaysBeforeEnd() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(
                reminderDaysBeforeEnd = -1,
            )
        }
    }

    @Test
    fun rejectsReminderWithoutEndDate() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(
                endDate = null,
                reminderDaysBeforeEnd = 30,
            )
        }
    }

    @Test
    fun keepsAuditTimestamps() {
        val createdAt = Instant.parse("2026-08-16T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-16T09:00:00Z")

        val warranty = createWarranty(
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(createdAt, warranty.createdAt)
        assertEquals(updatedAt, warranty.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(
                createdAt = Instant.parse("2026-08-16T09:00:00Z"),
                updatedAt = Instant.parse("2026-08-16T08:00:00Z"),
            )
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAtWithinSameMillisecond() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(
                createdAt = Instant.parse(
                    "2026-08-16T08:00:00.000000500Z"
                ),
                updatedAt = Instant.parse(
                    "2026-08-16T08:00:00.000000400Z"
                ),
            )
        }
    }

    @Test
    fun rejectsNegativeExpiringSoonDays() {
        assertFailsWith<IllegalArgumentException> {
            createWarranty(
                reminderDaysBeforeEnd = -1,
            )
        }
    }

    private fun createWarranty(
        id: WarrantyId = WarrantyId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        ),
        itemId: ItemId = ItemId(
            value = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
        ),
        startDate: LocalDate? = LocalDate(2025, 3, 15),
        endDate: LocalDate? = LocalDate(2028, 3, 15),
        type: WarrantyType? = WarrantyType.EXTENDED,
        providerName: String? = "Apple",
        notes: String? = "notas",
        reminderDaysBeforeEnd: Int? = null,
        createdAt: Instant = Instant.parse("2026-08-16T08:00:00Z"),
        updatedAt: Instant = createdAt,
    ): Warranty {
        return Warranty(
            id = id,
            itemId = itemId,
            startDate = startDate,
            endDate = endDate,
            type = type,
            providerName = providerName,
            notes = notes,
            reminderDaysBeforeEnd = reminderDaysBeforeEnd,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}