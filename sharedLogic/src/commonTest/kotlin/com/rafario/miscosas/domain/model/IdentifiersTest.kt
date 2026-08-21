package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.uuid.Uuid

class IdentifiersTest {

    @Test
    fun acceptsCanonicalHouseholdId() {
        val id = HouseholdId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )

        assertEquals(
            "550e8400-e29b-41d4-a716-446655440000",
            id.value)
    }

    @Test
    fun rejectsMalformedHouseholdId() {
        assertFailsWith<IllegalArgumentException> {
            HouseholdId(
                value = "not-a-uuid"
            )
        }
    }

    @Test
    fun rejectsNilHouseholdId() {
        assertFailsWith<IllegalArgumentException> {
            HouseholdId(
                value = "00000000-0000-0000-0000-000000000000"
            )
        }
    }

    @Test
    fun rejectsUppercaseHouseholdId() {
        assertFailsWith<IllegalArgumentException> {
            HouseholdId(
                value = "550E8400-E29B-41D4-A716-446655440000"
            )
        }
    }

    @Test
    fun rejectsHouseholdIdWithoutDashes() {
        assertFailsWith<IllegalArgumentException> {
            HouseholdId(
                value = "550e8400e29b41d4a716446655440000"
            )
        }
    }

    @Test
    fun generatesCanonicalNonNilHouseholdId() {
        val id = HouseholdId.generate()
        val parsedUuid = Uuid.parseHexDash(id.value)

        assertEquals(id.value, parsedUuid.toString())
        assertNotEquals(Uuid.NIL, parsedUuid)
    }

    @Test
    fun acceptsCanonicalItemId() {
        val id = ItemId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )

        assertEquals(
            "550e8400-e29b-41d4-a716-446655440000",
            id.value
        )
    }

    @Test
    fun rejectsMalformedItemId() {
        assertFailsWith<IllegalArgumentException> {
            ItemId(
                value = "not-a-uuid"
            )
        }
    }

    @Test
    fun rejectsNilItemId() {
        assertFailsWith<IllegalArgumentException> {
            ItemId(
                value = "00000000-0000-0000-0000-000000000000"
            )
        }
    }

    @Test
    fun rejectsUppercaseItemId() {
        assertFailsWith<IllegalArgumentException> {
            ItemId(
                value = "550E8400-E29B-41D4-A716-446655440000"
            )
        }
    }

    @Test
    fun rejectsItemIdWithoutDashes() {
        assertFailsWith<IllegalArgumentException> {
            ItemId(
                value = "550e8400e29b41d4a716446655440000"
            )
        }
    }

    @Test
    fun generatesCanonicalNonNilItemId() {
        val id = ItemId.generate()
        val parsedUuid = Uuid.parseHexDash(id.value)

        assertEquals(id.value, parsedUuid.toString())
        assertNotEquals(Uuid.NIL, parsedUuid)
    }

    @Test
    fun acceptsCanonicalWarrantyId() {
        val id = WarrantyId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )

        assertEquals("550e8400-e29b-41d4-a716-446655440000", id.value)
    }

    @Test
    fun rejectsMalformedWarrantyId() {
        assertFailsWith<IllegalArgumentException> {
            WarrantyId(
                value = "not-a-uuid"
            )
        }
    }

    @Test
    fun generatesCanonicalNonNilWarrantyId() {
        val id = WarrantyId.generate()
        val parsedUuid = Uuid.parseHexDash(id.value)

        assertEquals(id.value, parsedUuid.toString())
        assertNotEquals(Uuid.NIL, parsedUuid)
    }

    @Test
    fun returnsWarrantyIdValueAsString() {
        val id = WarrantyId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )

        assertEquals(id.value, id.toString())
    }

    @Test
    fun acceptsCanonicalDocumentId() {
        val id = DocumentId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )

        assertEquals("550e8400-e29b-41d4-a716-446655440000", id.value)
    }

    @Test
    fun rejectsMalformedDocumentId() {
        assertFailsWith<IllegalArgumentException> {
            DocumentId(
                value = "not-a-uuid"
            )
        }
    }

    @Test
    fun generatesCanonicalNonNilDocumentId() {
        val id = DocumentId.generate()
        val parsedUuid = Uuid.parseHexDash(id.value)

        assertEquals(id.value, parsedUuid.toString())
        assertNotEquals(Uuid.NIL, parsedUuid)
    }

    @Test
    fun returnsDocumentIdValueAsString() {
        val id = DocumentId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )

        assertEquals(id.value, id.toString())
    }
}
