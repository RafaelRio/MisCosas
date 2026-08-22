package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.HouseholdMemberEntity
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.HouseholdMember
import com.rafario.miscosas.domain.model.HouseholdRole
import com.rafario.miscosas.domain.model.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class HouseholdMemberEntityMapperTest {

    @Test
    fun mapsHouseholdMemberToEntityPreservingAllFieldsAndNanoseconds() {
        val member = HouseholdMember(
            householdId = HouseholdId(
                "550e8400-e29b-41d4-a716-446655440000",
            ),
            userId = UserId("firebase-user_A1b2C3"),
            role = HouseholdRole.OWNER,
            joinedAt = Instant.parse(
                "2026-08-21T08:00:00.000000400Z",
            ),
            updatedAt = Instant.parse(
                "2026-08-21T09:00:00.000000500Z",
            ),
        )

        val entity = member.toEntity()

        assertEquals(member.householdId.value, entity.householdId)
        assertEquals(member.userId.value, entity.userId)
        assertEquals(member.role.code, entity.roleCode)
        assertEquals(
            member.joinedAt.epochSeconds,
            entity.joinedAtEpochSeconds,
        )
        assertEquals(400, entity.joinedAtNanoseconds)
        assertEquals(
            member.updatedAt.epochSeconds,
            entity.updatedAtEpochSeconds,
        )
        assertEquals(500, entity.updatedAtNanoseconds)
    }

    @Test
    fun mapsHouseholdMemberEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val joinedAt = Instant.parse("2026-08-21T09:00:00.000000500Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00.000000800Z")

        val householdMemberEntity = HouseholdMemberEntity(
            householdId = "550e8400-e29b-41d4-a716-446655440000",
            userId = "firebase-user_A1b2C3",
            roleCode = HouseholdRole.MEMBER.code,
            joinedAtEpochSeconds = joinedAt.epochSeconds,
            joinedAtNanoseconds = joinedAt.nanosecondsOfSecond,
            updatedAtEpochSeconds = updatedAt.epochSeconds,
            updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
        )

        val domain = householdMemberEntity.toDomain()

        assertEquals(domain.householdId.value, householdMemberEntity.householdId)
        assertEquals(domain.userId.value, householdMemberEntity.userId)
        assertEquals(domain.role.code, householdMemberEntity.roleCode)
        assertEquals(
            domain.joinedAt.epochSeconds,
            householdMemberEntity.joinedAtEpochSeconds,
        )
        assertEquals(joinedAt, domain.joinedAt)
        assertEquals(updatedAt, domain.updatedAt)
        assertEquals(
            domain.updatedAt.epochSeconds,
            householdMemberEntity.updatedAtEpochSeconds,
        )
    }

    @Test
    fun rejectsUnknownStoredRoleCode() {
        val entity = HouseholdMemberEntity(
            householdId = "550e8400-e29b-41d4-a716-446655440000",
            userId = "firebase-user_A1b2C3",
            roleCode = "unknown-role",
            joinedAtEpochSeconds = 0,
            joinedAtNanoseconds = 0,
            updatedAtEpochSeconds = 0,
            updatedAtNanoseconds = 0,
        )

        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val entity = HouseholdMemberEntity(
            householdId = "550e8400-e29b-41d4-a716-446655440000",
            userId = "firebase-user_A1b2C3",
            roleCode = HouseholdRole.MEMBER.code,
            joinedAtEpochSeconds = 0,
            joinedAtNanoseconds = 0,
            updatedAtEpochSeconds = 0,
            updatedAtNanoseconds = 0,
        )

        assertFailsWith<IllegalStateException> {
            entity.copy(
                joinedAtNanoseconds = -1,
            ).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(
                updatedAtNanoseconds = 1_000_000_000,
            ).toDomain()
        }
    }
}
