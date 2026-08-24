package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.ReturnPeriodEntity
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.ReturnPeriod
import com.rafario.miscosas.domain.model.ReturnTrackingState
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

internal fun ReturnPeriod.toEntity(): ReturnPeriodEntity {
    return ReturnPeriodEntity(
        itemId = itemId.value,
        seller = seller,
        deadlineEpochDay = deadline.toEpochDays(),
        createdAtEpochSeconds = createdAt.epochSeconds,
        createdAtNanoseconds = createdAt.nanosecondsOfSecond,
        updatedAtEpochSeconds = updatedAt.epochSeconds,
        updatedAtNanoseconds = updatedAt.nanosecondsOfSecond,
        trackingStateCode = trackingState.code,
    )
}

internal fun ReturnPeriodEntity.toDomain(): ReturnPeriod {
    val trackingState = checkNotNull(ReturnTrackingState.fromCodeOrNull(trackingStateCode)) {
        "Invalid tracking state code: $trackingStateCode"
    }
    return ReturnPeriod(
        itemId = ItemId(itemId),
        seller = seller,
        deadline = localDateFromEpochDay(
            epochDay = deadlineEpochDay,
            fieldName = "ReturnPeriodEntity.deadline",
        ),
        createdAt = instantFromEpochColumns(
            epochSeconds = createdAtEpochSeconds,
            nanosecondsOfSecond = createdAtNanoseconds,
            fieldName = "ReturnPeriodEntity.createdAt",
        ),
        updatedAt = instantFromEpochColumns(
            epochSeconds = updatedAtEpochSeconds,
            nanosecondsOfSecond = updatedAtNanoseconds,
            fieldName = "ReturnPeriodEntity.updatedAt",
        ),
        trackingState = trackingState,
    )
}