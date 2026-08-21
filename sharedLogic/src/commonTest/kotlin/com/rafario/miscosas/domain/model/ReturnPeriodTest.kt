package com.rafario.miscosas.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class ReturnPeriodTest {

    @Test
    fun keepsItemIdentityAndDeadline() {
        val itemId = ItemId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        )
        val deadline = LocalDate(2026, 9, 15)

        val returnPeriod = createReturnPeriod(itemId = itemId, deadline = deadline)

        assertEquals(itemId, returnPeriod.itemId)
        assertEquals(deadline, returnPeriod.deadline)
    }

    @Test
    fun keepsSeller() {
        val seller = "Amazon"

        val returnPeriod = createReturnPeriod(
            seller = seller,
        )

        assertEquals(seller, returnPeriod.seller)
    }

    @Test
    fun allowsUnknownSeller() {
        val returnPeriod = createReturnPeriod(seller = null)
        assertNull(returnPeriod.seller)
    }

    @Test
    fun rejectsBlankSeller() {
        assertFailsWith<IllegalArgumentException> {
            createReturnPeriod(seller = "     ")
        }
    }

    @Test
    fun keepsAuditTimestamps() {
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00Z")

        val returnPeriod = createReturnPeriod(createdAt = createdAt, updatedAt = updatedAt)

        assertEquals(createdAt, returnPeriod.createdAt)
        assertEquals(updatedAt, returnPeriod.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        assertFailsWith<IllegalArgumentException> {
            val createdAt = Instant.parse("2026-08-21T08:00:00Z")
            val updatedAt = Instant.parse("2026-08-20T09:00:00Z")

            createReturnPeriod(createdAt = createdAt, updatedAt = updatedAt)
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAtWithinSameMillisecond() {
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-20T09:00:00Z")

        assertFailsWith<IllegalArgumentException> {
            createReturnPeriod(
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    @Test
    fun returnsDaysRemainingBeforeDeadline() {
        val returnPeriod = createReturnPeriod(
            deadline = LocalDate(2026, 9, 15),
        )

        val days = returnPeriod.daysRemainingOn(
            date = LocalDate(2026, 9, 3),
        )

        assertEquals(12, days)
    }

    @Test
    fun returnsZeroOnDeadline() {
        val returnPeriod = createReturnPeriod(
            deadline = LocalDate(2026, 9, 15),
        )

        val days = returnPeriod.daysRemainingOn(
            date = LocalDate(2026, 9, 15),
        )

        assertEquals(0, days)
    }

    @Test
    fun returnsNegativeDaysAfterDeadline() {
        val returnPeriod = createReturnPeriod(
            deadline = LocalDate(2026, 9, 15),
        )

        val days = returnPeriod.daysRemainingOn(
            date = LocalDate(2026, 9, 16),
        )

        assertEquals(-1, days)
    }

    @Test
    fun returnsOpenStatusOutsideEndingSoonWindow() {
        val returnPeriod = createReturnPeriod(
            deadline = LocalDate(2026, 9, 15),
        )

        val status = returnPeriod.statusOn(
            date = LocalDate(2026, 8, 15),
            endingSoonThresholdDays = 30,
        )

        assertEquals(ReturnWindowStatus.OPEN, status)
    }

    @Test
    fun returnsEndingSoonStatusAtThreshold() {
        val returnPeriod = createReturnPeriod(
            deadline = LocalDate(2026, 9, 15),
        )

        val status = returnPeriod.statusOn(
            date = LocalDate(2026, 8, 16),
            endingSoonThresholdDays = 30,
        )

        assertEquals(ReturnWindowStatus.ENDING_SOON, status)
    }

    @Test
    fun returnsEndingSoonStatusOnDeadline() {
        val returnPeriod = createReturnPeriod(
            deadline = LocalDate(2026, 9, 15),
        )

        val status = returnPeriod.statusOn(
            date = LocalDate(2026, 9, 15),
            endingSoonThresholdDays = 30,
        )

        assertEquals(ReturnWindowStatus.ENDING_SOON, status)
    }

    @Test
    fun returnsExpiredStatusAfterDeadline() {
        val returnPeriod = createReturnPeriod(
            deadline = LocalDate(2026, 9, 15),
        )

        val status = returnPeriod.statusOn(
            date = LocalDate(2026, 9, 16),
            endingSoonThresholdDays = 30,
        )

        assertEquals(ReturnWindowStatus.EXPIRED, status)
    }

    @Test
    fun rejectsNegativeEndingSoonDays() {
        val returnPeriod = createReturnPeriod()

        assertFailsWith<IllegalArgumentException> {
            returnPeriod.statusOn(
                date = LocalDate(2026, 8, 16),
                endingSoonThresholdDays = -1,
            )
        }
    }

    @Test
    fun usesDeadlineAsOnlyEndingSoonDayWhenThresholdIsZero() {
        val deadline = LocalDate(2026, 9, 15)
        val returnPeriod = createReturnPeriod(
            deadline = deadline,
        )

        assertEquals(
            ReturnWindowStatus.OPEN,
            returnPeriod.statusOn(
                date = LocalDate(2026, 9, 14),
                endingSoonThresholdDays = 0,
            ),
        )
        assertEquals(
            ReturnWindowStatus.ENDING_SOON,
            returnPeriod.statusOn(
                date = deadline,
                endingSoonThresholdDays = 0,
            ),
        )
    }

    @Test
    fun keepsTrackingState() {
        val returnPeriod = createReturnPeriod(
            trackingState = ReturnTrackingState.RETURNED,
        )

        assertEquals(
            ReturnTrackingState.RETURNED,
            returnPeriod.trackingState,
        )
    }

    private fun createReturnPeriod(
        itemId: ItemId = ItemId(
            value = "550e8400-e29b-41d4-a716-446655440000"
        ),
        deadline: LocalDate = LocalDate(2026, 9, 15),
        seller: String? = null,
        createdAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        updatedAt: Instant = createdAt,
        trackingState: ReturnTrackingState = ReturnTrackingState.TRACKING,
    ): ReturnPeriod =
        ReturnPeriod(
            itemId = itemId,
            deadline = deadline,
            seller = seller,
            createdAt = createdAt,
            updatedAt = updatedAt,
            trackingState = trackingState
        )
}