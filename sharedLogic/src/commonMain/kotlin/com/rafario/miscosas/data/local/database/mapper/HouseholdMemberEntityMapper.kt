package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.HouseholdMemberEntity
import com.rafario.miscosas.domain.model.HouseholdId
import com.rafario.miscosas.domain.model.HouseholdMember
import com.rafario.miscosas.domain.model.HouseholdRole
import com.rafario.miscosas.domain.model.UserId

internal fun HouseholdMember.toEntity(): HouseholdMemberEntity {
    return HouseholdMemberEntity(
        householdId = householdId.value,
        userId = userId.value,
        roleCode = role.code,
        joinedAtEpochSeconds = joinedAt.epochSeconds,
        joinedAtNanoseconds = joinedAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
    )
}

internal fun HouseholdMemberEntity.toDomain(): HouseholdMember {
    val role = checkNotNull(HouseholdRole.fromCodeOrNull(roleCode)) {
        "Unknown role code: $roleCode"
    }
    return HouseholdMember(
        householdId = HouseholdId(householdId),
        userId = UserId(userId),
        role = role,
        joinedAt = instantFromEpochColumns(
            epochSeconds = joinedAtEpochSeconds,
            nanosecondsOfSecond = joinedAtNanoseconds,
            fieldName = "joinedAt",
        ),
        updatedAt = instantFromEpochColumns(
            epochSeconds = updatedAtEpochSeconds,
            nanosecondsOfSecond = updatedAtNanoseconds,
            fieldName = "updatedAt",
        )
    )
}