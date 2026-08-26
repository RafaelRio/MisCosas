package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.Household
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.HouseholdMember
import com.rafario.miscosas.domain.model.HouseholdRole
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.HouseholdRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class CreateHouseholdUseCaseTest {

    @Test
    fun createsHouseholdAndOwnerMembershipWithSameInstant() = runTest {
        val repository = RecordingHouseholdRepository()
        val householdId =
            HouseholdId("550e8400-e29b-41d4-a716-446655440001")
        val currentUserId = UserId("firebase-user-123")
        val now = Instant.parse("2026-08-26T10:15:30.000000789Z")

        val fixedClock = object : Clock {
            override fun now() = now
        }

        val useCase = CreateHouseholdUseCase(
            householdRepository = repository,
            clock = fixedClock,
            generateHouseholdId = { householdId },
        )

        val result = useCase(
            name = "Casa Madrid",
            currentUserId = currentUserId,
        )

        assertEquals(householdId, result)

        assertEquals(
            Household(
                id = householdId,
                name = "Casa Madrid",
                createdBy = currentUserId,
                createdAt = now,
                updatedAt = now,
            ),
            repository.createdHousehold,
        )

        assertEquals(
            HouseholdMember(
                householdId = householdId,
                userId = currentUserId,
                role = HouseholdRole.OWNER,
                joinedAt = now,
                updatedAt = now,
            ),
            repository.createdOwnerMembership,
        )
    }

    private class RecordingHouseholdRepository : HouseholdRepository {

        var createdHousehold: Household? = null
            private set

        var createdOwnerMembership: HouseholdMember? = null
            private set

        override suspend fun create(
            household: Household,
            ownerMembership: HouseholdMember,
        ) {
            createdHousehold = household
            createdOwnerMembership = ownerMembership
        }
    }
}