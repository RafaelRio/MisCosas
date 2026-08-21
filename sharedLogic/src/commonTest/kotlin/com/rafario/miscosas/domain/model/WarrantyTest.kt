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
                id = warrantyId, itemId = itemId, startDate = startDate, endDate = endDate
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
                notes = null,
                documentId = null
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
        val warranty = createWarranty()

        assertFailsWith<IllegalArgumentException> {
            warranty.statusOn(
                date = LocalDate(2026, 8, 16),
                expiringSoonDays = -1,
            )
        }
    }

    @Test
    fun returnsUnknownStatusWhenEndDateIsUnknown() {
        val warranty = createWarranty(
            endDate = null,
        )

        val status = warranty.statusOn(
            date = LocalDate(2026, 8, 16),
            expiringSoonDays = 30,
        )

        assertEquals(WarrantyStatus.UNKNOWN, status)
    }

    @Test
    fun returnsActiveStatusOutsideExpiringSoonWindow() {
        val warranty = createWarranty(
            endDate = LocalDate(2026, 9, 15),
        )

        val status = warranty.statusOn(
            date = LocalDate(2026, 8, 15),
            expiringSoonDays = 30,
        )

        assertEquals(WarrantyStatus.ACTIVE, status)
    }

    @Test
    fun returnsExpiringSoonStatusAtThreshold() {
        val warranty = createWarranty(
            endDate = LocalDate(2026, 9, 15),
        )

        val status = warranty.statusOn(
            date = LocalDate(2026, 8, 16),
            expiringSoonDays = 30,
        )

        assertEquals(WarrantyStatus.EXPIRING_SOON, status)
    }

    @Test
    fun returnsExpiringSoonStatusOnEndDate() {
        val warranty = createWarranty(
            endDate = LocalDate(2026, 9, 15),
        )

        val status = warranty.statusOn(
            date = LocalDate(2026, 9, 15),
            expiringSoonDays = 30,
        )

        assertEquals(WarrantyStatus.EXPIRING_SOON, status)
    }

    @Test
    fun returnsExpiredStatusAfterEndDate() {
        val warranty = createWarranty(
            endDate = LocalDate(2026, 9, 15),
        )

        val status = warranty.statusOn(
            date = LocalDate(2026, 9, 16),
            expiringSoonDays = 30,
        )

        assertEquals(WarrantyStatus.EXPIRED, status)
    }

    @Test
    fun usesEndDateAsOnlyExpiringDayWhenThresholdIsZero() {
        val endDate = LocalDate(2026, 9, 15)
        val warranty = createWarranty(endDate = endDate)

        assertEquals(
            WarrantyStatus.ACTIVE,
            warranty.statusOn(
                date = LocalDate(2026, 9, 14),
                expiringSoonDays = 0,
            ),
        )
        assertEquals(
            WarrantyStatus.EXPIRING_SOON,
            warranty.statusOn(
                date = endDate,
                expiringSoonDays = 0,
            ),
        )
    }

    @Test
    fun keepsAssociatedDocumentId() {
        val documentId = DocumentId("550e8400-e29b-41d4-a716-446655440002")

        val warranty = createWarranty(documentId = documentId)

        assertEquals(documentId, warranty.documentId)
    }

    @Test
    fun allowsWarrantyWithOnlyDocument() {
        val documentId = DocumentId(
            value = "550e8400-e29b-41d4-a716-446655440002",
        )
        val warranty = createWarranty(
            startDate = null,
            endDate = null,
            type = null,
            providerName = null,
            notes = null,
            documentId = documentId,
        )
        assertEquals(documentId, warranty.documentId)
    }

    private fun createWarranty(
        id: WarrantyId = WarrantyId(
            value = "550e8400-e29b-41d4-a716-446655440000",
        ),
        itemId: ItemId = ItemId(
            value = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
        ),
        documentId: DocumentId? = null,
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
            documentId = documentId,
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
