package com.rafario.miscosas.domain.model

import kotlin.time.Instant

data class HouseholdMember(
    val householdId: HouseholdId,
    val userId: UserId,
    val role: HouseholdRole,
    val joinedAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(updatedAt >= joinedAt) {
            "HouseholdMember updatedAt must not be before joinedAt"
        }
    }
}
