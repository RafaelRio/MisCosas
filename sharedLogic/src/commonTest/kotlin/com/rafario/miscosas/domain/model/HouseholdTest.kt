package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class HouseholdTest {

    @Test
    fun keepsHouseholdInformation() {
        val id = HouseholdId(
            "550e8400-e29b-41d4-a716-446655440000",
        )
        val name = "Casa familiar"
        val createdBy = UserId("firebase-user_A1b2C3")
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00Z")

        val household = Household(
            id = id,
            name = name,
            createdBy = createdBy,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(id, household.id)
        assertEquals(name, household.name)
        assertEquals(createdBy, household.createdBy)
        assertEquals(createdAt, household.createdAt)
        assertEquals(updatedAt, household.updatedAt)
    }

    @Test
    fun rejectsBlankName() {
        assertFailsWith<IllegalArgumentException> {
            createHousehold(name = "   ")
        }
    }

    @Test
    fun allowsUpdatedAtEqualToCreatedAt() {
        val instant = Instant.parse("2026-08-21T08:00:00Z")

        val household = createHousehold(
            createdAt = instant,
            updatedAt = instant,
        )

        assertEquals(instant, household.createdAt)
        assertEquals(instant, household.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        assertFailsWith<IllegalArgumentException> {
            createHousehold(
                createdAt = Instant.parse("2026-08-21T08:00:00Z"),
                updatedAt = Instant.parse("2026-08-20T08:00:00Z"),
            )
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAtWithinSameMillisecond() {
        assertFailsWith<IllegalArgumentException> {
            createHousehold(
                createdAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
                updatedAt = Instant.parse("2026-08-21T08:00:00.000000400Z"),
            )
        }
    }

    private fun createHousehold(
        id: HouseholdId =
            HouseholdId("550e8400-e29b-41d4-a716-446655440000"),
        name: String = "Casa familiar",
        createdBy: UserId = UserId("firebase-user_A1b2C3"),
        createdAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        updatedAt: Instant = createdAt,
    ): Household {
        return Household(
            id = id,
            name = name,
            createdBy = createdBy,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
