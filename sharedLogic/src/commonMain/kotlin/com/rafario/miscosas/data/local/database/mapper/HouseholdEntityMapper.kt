package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.domain.model.Household
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.UserId

internal fun Household.toEntity(): HouseholdEntity {
    return HouseholdEntity(
        id = id.value,
        name = name,
        createdBy = createdBy.value,
        createdAtEpochSeconds = createdAt.epochSeconds,
        createdAtNanoseconds = createdAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
    )
}

internal fun HouseholdEntity.toDomain(): Household {
    return Household(
        id = HouseholdId(id),
        name = name,
        createdBy = UserId(createdBy),
        createdAt = instantFromEpochColumns(
            epochSeconds = createdAtEpochSeconds,
            nanosecondsOfSecond = createdAtNanoseconds,
            fieldName = "HouseholdEntity.createdAt",
        ),
        updatedAt = instantFromEpochColumns(
            epochSeconds = updatedAtEpochSeconds,
            nanosecondsOfSecond = updatedAtNanoseconds,
            fieldName = "HouseholdEntity.updatedAt",
        ),
    )
}