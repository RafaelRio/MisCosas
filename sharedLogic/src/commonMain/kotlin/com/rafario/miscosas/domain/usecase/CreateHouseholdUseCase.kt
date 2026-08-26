package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.Household
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.HouseholdMember
import com.rafario.miscosas.domain.model.HouseholdRole
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.HouseholdRepository
import kotlin.time.Clock

internal class CreateHouseholdUseCase(
    private val householdRepository: HouseholdRepository,
    private val clock: Clock = Clock.System,
    private val generateHouseholdId: () -> HouseholdId = {
        HouseholdId.generate()
    },
) {

    suspend operator fun invoke(
        name: String,
        currentUserId: UserId,
    ): HouseholdId {
        val householdId = generateHouseholdId()
        val now = clock.now()

        val household = Household(
            id = householdId,
            name = name,
            createdBy = currentUserId,
            createdAt = now,
            updatedAt = now,
        )

        val ownerMembership = HouseholdMember(
            householdId = householdId,
            userId = currentUserId,
            role = HouseholdRole.OWNER,
            joinedAt = now,
            updatedAt = now,
        )

        householdRepository.create(
            household = household,
            ownerMembership = ownerMembership,
        )

        return householdId
    }
}
