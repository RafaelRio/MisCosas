package com.rafario.miscosas.data.local.database.mapper

import kotlin.time.Instant

internal fun instantFromEpochColumns(
    epochSeconds: Long,
    nanosecondsOfSecond: Int,
    fieldName: String,
): Instant {
    check(nanosecondsOfSecond in 0..999_999_999) {
        "$fieldName nanoseconds must be between 0 and 999999999"
    }

    return Instant.fromEpochSeconds(
        epochSeconds = epochSeconds,
        nanosecondAdjustment = nanosecondsOfSecond,
    )
}