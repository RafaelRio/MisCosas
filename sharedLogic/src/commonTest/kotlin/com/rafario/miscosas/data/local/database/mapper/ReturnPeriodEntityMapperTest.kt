package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.ReturnPeriodEntity
import com.rafario.miscosas.domain.model.ItemId
import com.rafario.miscosas.domain.model.ReturnPeriod
import com.rafario.miscosas.domain.model.ReturnTrackingState
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class ReturnPeriodEntityMapperTest {

    @Test
    fun mapsReturnPeriodToEntityPreservingAllFieldsAndNanoseconds() {
        val returnPeriod = ReturnPeriod(
            itemId = ItemId("550e8200-e29b-41d4-a716-446655440010"),
            seller = "Proveedor",
            deadline = LocalDate(2026, 4, 20),
            createdAt = Instant.parse("2026-08-21T08:00:00.000000400Z"),
            updatedAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
            trackingState = ReturnTrackingState.TRACKING,
        )
        val entity = returnPeriod.toEntity()

        assertEquals(returnPeriod.itemId.value, entity.itemId)
        assertEquals(returnPeriod.seller, entity.seller)
        assertEquals(returnPeriod.deadline.toEpochDays(), entity.deadlineEpochDay)
        assertEquals(returnPeriod.createdAt.epochSeconds, entity.createdAtEpochSeconds)
        assertEquals(returnPeriod.createdAt.nanosecondsOfSecond, entity.createdAtNanoseconds)
        assertEquals(returnPeriod.updatedAt.epochSeconds, entity.updatedAtEpochSeconds)
        assertEquals(returnPeriod.updatedAt.nanosecondsOfSecond, entity.updatedAtNanoseconds)
        assertEquals(returnPeriod.trackingState.code, entity.trackingStateCode)
    }

    @Test
    fun mapsReturnPeriodEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val entity = createReturnPeriodEntity()
        val returnPeriod = entity.toDomain()

        assertEquals(ItemId(entity.itemId), returnPeriod.itemId)
        assertEquals(entity.seller, returnPeriod.seller)
        assertEquals(LocalDate.fromEpochDays(entity.deadlineEpochDay), returnPeriod.deadline)
        assertEquals(
            Instant.fromEpochSeconds(
                entity.createdAtEpochSeconds, entity.createdAtNanoseconds.toLong()
            ), returnPeriod.createdAt
        )
        assertEquals(
            Instant.fromEpochSeconds(
                entity.updatedAtEpochSeconds, entity.updatedAtNanoseconds.toLong()
            ), returnPeriod.updatedAt
        )
        assertEquals(
            ReturnTrackingState.fromCodeOrNull(entity.trackingStateCode), returnPeriod.trackingState
        )
    }

    @Test
    fun rejectsUnknownStoredTrackingStateCode() {
        val entity = createReturnPeriodEntity(
            trackingStateCode = "unknown-state",
        )
        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsInvalidStoredDeadlineEpochDay() {
        val entity = createReturnPeriodEntity(
            deadlineEpochDay = Long.MAX_VALUE,
        )
        assertFailsWith<IllegalStateException> {
            entity.toDomain()
        }
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val entity = createReturnPeriodEntity()

        assertFailsWith<IllegalStateException> {
            entity.copy(createdAtNanoseconds = -1).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(updatedAtNanoseconds = 1_000_000_000).toDomain()
        }
    }

    private fun createReturnPeriodEntity(
        itemId: String = "550e8200-e29b-41d4-a716-446655440010",
        seller: String? = "Proveedor",
        deadlineEpochDay: Long = LocalDate(2026, 4, 20).toEpochDays(),
        createdAtEpochSeconds: Long = 1_000L,
        createdAtNanoseconds: Int = 400,
        updatedAtEpochSeconds: Long = 1_000L,
        updatedAtNanoseconds: Int = 500,
        trackingStateCode: String = "tracking",
    ): ReturnPeriodEntity = ReturnPeriodEntity(
        itemId = itemId,
        seller = seller,
        deadlineEpochDay = deadlineEpochDay,
        createdAtEpochSeconds = createdAtEpochSeconds,
        createdAtNanoseconds = createdAtNanoseconds,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
        updatedAtNanoseconds = updatedAtNanoseconds,
        trackingStateCode = trackingStateCode,
    )
}