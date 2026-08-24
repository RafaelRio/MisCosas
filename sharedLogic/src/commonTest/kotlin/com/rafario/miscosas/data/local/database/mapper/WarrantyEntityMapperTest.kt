package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.WarrantyEntity
import com.rafario.miscosas.domain.model.DocumentId
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.Warranty
import com.rafario.miscosas.domain.model.WarrantyId
import com.rafario.miscosas.domain.model.WarrantyType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class WarrantyEntityMapperTest {

    @Test
    fun mapsWarrantyToEntityPreservingAllFieldsAndNanoseconds() {
        val warranty = Warranty(
            id = WarrantyId("550e8400-e29b-41d4-a716-446655440000"),
            itemId = ItemId("550e8200-e29b-41d4-a716-446655440010"),
            documentId = DocumentId("553e8400-e29b-41d4-a716-446655440000"),
            startDate = LocalDate(2026, 4, 20),
            endDate = LocalDate(2026, 5, 20),
            type = WarrantyType.EXTENDED,
            providerName = "provider",
            notes = "notas",
            reminderDaysBeforeEnd = 1,
            createdAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
            updatedAt = Instant.parse("2026-08-21T08:00:00.000000900Z"),
        )
        val entity = warranty.toEntity()

        assertEquals(warranty.id.value, entity.id)
        assertEquals(warranty.itemId.value, entity.itemId)
        assertEquals(warranty.documentId?.value, entity.documentId)
        assertEquals(warranty.startDate?.toEpochDays(), entity.startDateEpochDay)
        assertEquals(warranty.endDate?.toEpochDays(), entity.endDateEpochDay)
        assertEquals(warranty.type?.code, entity.typeCode)
        assertEquals(warranty.providerName, entity.providerName)
        assertEquals(warranty.notes, entity.notes)
        assertEquals(warranty.reminderDaysBeforeEnd, entity.reminderDaysBeforeEnd)
        assertEquals(warranty.createdAt.epochSeconds, entity.createdAtEpochSeconds)
        assertEquals(warranty.createdAt.nanosecondsOfSecond, entity.createdAtNanoseconds)
        assertEquals(warranty.updatedAt.epochSeconds, entity.updatedAtEpochSeconds)
        assertEquals(warranty.updatedAt.nanosecondsOfSecond, entity.updatedAtNanoseconds)
    }

    @Test
    fun mapsWarrantyEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val entity = createWarrantyEntity()
        val warranty = entity.toDomain()

        assertEquals(WarrantyId(entity.id), warranty.id)
        assertEquals(ItemId(entity.itemId), warranty.itemId)
        assertEquals(DocumentId(entity.documentId!!), warranty.documentId)
        assertEquals(LocalDate.fromEpochDays(entity.startDateEpochDay!!), warranty.startDate)
        assertEquals(LocalDate.fromEpochDays(entity.endDateEpochDay!!), warranty.endDate)
        assertEquals(WarrantyType.fromCodeOrNull(entity.typeCode!!), warranty.type)
        assertEquals(entity.providerName, warranty.providerName)
        assertEquals(entity.notes, warranty.notes)
        assertEquals(entity.reminderDaysBeforeEnd, warranty.reminderDaysBeforeEnd)
        assertEquals(Instant.fromEpochSeconds(entity.createdAtEpochSeconds, entity.createdAtNanoseconds.toLong()), warranty.createdAt)
        assertEquals(Instant.fromEpochSeconds(entity.updatedAtEpochSeconds, entity.updatedAtNanoseconds.toLong()), warranty.updatedAt)
    }

    @Test
    fun rejectsUnknownStoredWarrantyTypeCode() {
        val entity = createWarrantyEntity(
            typeCode = "unknown-type",
        )

        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsStoredWarrantyDatesOutsideValidRange() {
        val entity = createWarrantyEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(
                startDateEpochDay = Long.MAX_VALUE,
            ).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(
                endDateEpochDay = Long.MAX_VALUE,
            ).toDomain()
        }
    }

    @Test
    fun mapsNullableWarrantyFields() {
        val entity = createWarrantyEntity(
            documentId = null,
            startDateEpochDay = null,
            endDateEpochDay = null,
            typeCode = null,
            providerName = null,
            notes = "Garantía sin información adicional",
            reminderDaysBeforeEnd = null,
        )

        val warranty = entity.toDomain()

        assertNull(warranty.documentId)
        assertNull(warranty.startDate)
        assertNull(warranty.endDate)
        assertNull(warranty.type)
        assertNull(warranty.providerName)
        assertEquals("Garantía sin información adicional", warranty.notes)
        assertNull(warranty.reminderDaysBeforeEnd)
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val entity = createWarrantyEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(createdAtNanoseconds = -1).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(updatedAtNanoseconds = 1_000_000_000).toDomain()
        }
    }

    private fun createWarrantyEntity(
        id: String = "550e8400-e29b-41d4-a716-446655440000",
        itemId: String = "550e8400-e29b-41d4-a716-446655440010",
        documentId: String? = "550e8400-e29b-41d4-a716-446655440020",
        startDateEpochDay: Long? = LocalDate(2026, 4, 20).toEpochDays(),
        endDateEpochDay: Long? = LocalDate(2026, 5, 20).toEpochDays(),
        typeCode: String? = WarrantyType.EXTENDED.code,
        providerName: String? = "Proveedor",
        notes: String? = "Garantía ampliada",
        reminderDaysBeforeEnd: Int? = 30,
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 500,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 900,
    ): WarrantyEntity {
        return WarrantyEntity(
            id = id,
            itemId = itemId,
            documentId = documentId,
            startDateEpochDay = startDateEpochDay,
            endDateEpochDay = endDateEpochDay,
            typeCode = typeCode,
            providerName = providerName,
            notes = notes,
            reminderDaysBeforeEnd = reminderDaysBeforeEnd,
            createdAtEpochSeconds = createdAtEpochSeconds,
            createdAtNanoseconds = createdAtNanoseconds,
            updatedAtEpochSeconds = updatedAtEpochSeconds,
            updatedAtNanoseconds = updatedAtNanoseconds,
        )
    }
}
