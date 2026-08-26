package com.rafario.miscosas.domain.repository

import com.rafario.miscosas.domain.model.Household
import com.rafario.miscosas.domain.model.HouseholdMember

internal interface HouseholdRepository {

    suspend fun create(
        household: Household,
        ownerMembership: HouseholdMember,
    )
}