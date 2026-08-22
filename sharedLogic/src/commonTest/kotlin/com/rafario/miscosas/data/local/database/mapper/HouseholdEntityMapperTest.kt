package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.domain.model.Household
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class HouseholdEntityMapperTest {

    @Test
    fun mapsHouseholdToEntityPreservingAllFieldsAndNanoseconds() {
        val createdAt = Instant.parse("2026-08-22T10:00:00.000000400Z")
        val updatedAt = Instant.parse("2026-08-22T10:00:00.000000500Z")
        val household = Household(
            id = HouseholdId("550e8400-e29b-41d4-a716-446655440000"),
            name = "Casa familiar",
            createdBy = UserId("firebase-user_A1b2C3"),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        val entity = household.toEntity()

        assertEquals(
            "550e8400-e29b-41d4-a716-446655440000",
            entity.id,
        )
        assertEquals("Casa familiar", entity.name)
        assertEquals("firebase-user_A1b2C3", entity.createdBy)
        assertEquals(createdAt.epochSeconds, entity.createdAtEpochSeconds)
        assertEquals(400, entity.createdAtNanoseconds)
        assertEquals(updatedAt.epochSeconds, entity.updatedAtEpochSeconds)
        assertEquals(500, entity.updatedAtNanoseconds)
    }

    @Test
    fun mapsHouseholdEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val createdAt = Instant.parse("2026-08-22T10:00:00.000000400Z")
        val updatedAt = Instant.parse("2026-08-22T10:00:00.000000500Z")
        val entity = HouseholdEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            name = "Casa familiar",
            createdBy = "firebase-user_A1b2C3",
            createdAtEpochSeconds = createdAt.epochSeconds,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = updatedAt.epochSeconds,
            updatedAtNanoseconds = 500,
        )

        val household = entity.toDomain()

        assertEquals(
            HouseholdId("550e8400-e29b-41d4-a716-446655440000"),
            household.id,
        )
        assertEquals("Casa familiar", household.name)
        assertEquals(UserId("firebase-user_A1b2C3"), household.createdBy)
        assertEquals(createdAt, household.createdAt)
        assertEquals(updatedAt, household.updatedAt)
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val instant = Instant.parse("2026-08-22T10:00:00Z")
        val entity = HouseholdEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            name = "Casa familiar",
            createdBy = "firebase-user_A1b2C3",
            createdAtEpochSeconds = instant.epochSeconds,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = instant.epochSeconds,
            updatedAtNanoseconds = 500,
        )

        assertFailsWith<IllegalStateException> {
            entity.copy(createdAtNanoseconds = -1).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(updatedAtNanoseconds = 1_000_000_000).toDomain()
        }
    }
}