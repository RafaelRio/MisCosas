package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import kotlin.time.Instant

internal fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id.value,
        displayName = displayName,
        createdAtEpochSeconds = createdAt.epochSeconds,
        createdAtNanoseconds = createdAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
    )
}

internal fun UserEntity.toDomain(): User {
    check(createdAtNanoseconds in 0..999_999_999) {
        "UserEntity createdAtNanoseconds must be between 0 and 999999999"
    }

    check(updatedAtNanoseconds in 0..999_999_999) {
        "UserEntity updatedAtNanoseconds must be between 0 and 999999999"
    }
    return User(
        id = UserId(id),
        displayName = displayName,
        createdAt = Instant.fromEpochSeconds(
            epochSeconds = createdAtEpochSeconds,
            nanosecondAdjustment = createdAtNanoseconds,
        ),
        updatedAt = Instant.fromEpochSeconds(
            epochSeconds = updatedAtEpochSeconds,
            nanosecondAdjustment = updatedAtNanoseconds,
        ),
    )
}