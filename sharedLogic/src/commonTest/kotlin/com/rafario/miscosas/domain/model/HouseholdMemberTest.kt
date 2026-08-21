package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class HouseholdMemberTest {

    @Test
    fun keepsHouseholdMembershipInformation() {
        val householdId = HouseholdId(
            "550e8400-e29b-41d4-a716-446655440000",
        )
        val userId = UserId("firebase-user_A1b2C3")
        val role = HouseholdRole.OWNER
        val joinedAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00Z")

        val member = HouseholdMember(
            householdId = householdId,
            userId = userId,
            role = role,
            joinedAt = joinedAt,
            updatedAt = updatedAt,
        )

        assertEquals(householdId, member.householdId)
        assertEquals(userId, member.userId)
        assertEquals(role, member.role)
        assertEquals(joinedAt, member.joinedAt)
        assertEquals(updatedAt, member.updatedAt)
    }

    @Test
    fun allowsUpdatedAtEqualToJoinedAt() {
        val instant = Instant.parse("2026-08-21T08:00:00Z")

        val member = createHouseholdMember(
            joinedAt = instant,
            updatedAt = instant,
        )

        assertEquals(instant, member.joinedAt)
        assertEquals(instant, member.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeJoinedAt() {
        assertFailsWith<IllegalArgumentException> {
            createHouseholdMember(
                joinedAt = Instant.parse("2026-08-21T08:00:00Z"),
                updatedAt = Instant.parse("2026-08-20T08:00:00Z"),
            )
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeJoinedAtWithinSameMillisecond() {
        assertFailsWith<IllegalArgumentException> {
            createHouseholdMember(
                joinedAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
                updatedAt = Instant.parse("2026-08-21T08:00:00.000000400Z"),
            )
        }
    }

    private fun createHouseholdMember(
        householdId: HouseholdId =
            HouseholdId("550e8400-e29b-41d4-a716-446655440000"),
        userId: UserId = UserId("firebase-user_A1b2C3"),
        role: HouseholdRole = HouseholdRole.MEMBER,
        joinedAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        updatedAt: Instant = joinedAt,
    ): HouseholdMember {
        return HouseholdMember(
            householdId = householdId,
            userId = userId,
            role = role,
            joinedAt = joinedAt,
            updatedAt = updatedAt,
        )
    }
}