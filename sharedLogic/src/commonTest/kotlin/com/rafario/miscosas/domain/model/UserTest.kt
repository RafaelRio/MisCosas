package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class UserTest {

    @Test
    fun keepsUserProfileInformation() {
        val id = UserId("firebase-user_A1b2C3")
        val displayName = "Rafael"
        val createdAt = Instant.parse("2026-08-21T08:00:00Z")
        val updatedAt = Instant.parse("2026-08-21T09:00:00Z")

        val user = User(
            id = id,
            displayName = displayName,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals(id, user.id)
        assertEquals(displayName, user.displayName)
        assertEquals(createdAt, user.createdAt)
        assertEquals(updatedAt, user.updatedAt)
    }

    @Test
    fun rejectsBlankDisplayName() {
        assertFailsWith<IllegalArgumentException> {
            createUser(displayName = "   ")
        }
    }

    @Test
    fun allowsUpdatedAtEqualToCreatedAt() {
        val instant = Instant.parse("2026-08-21T08:00:00Z")

        val user = createUser(
            createdAt = instant,
            updatedAt = instant,
        )

        assertEquals(instant, user.createdAt)
        assertEquals(instant, user.updatedAt)
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAt() {
        assertFailsWith<IllegalArgumentException> {
            createUser(
                createdAt = Instant.parse("2026-08-21T08:00:00Z"),
                updatedAt = Instant.parse("2026-08-20T08:00:00Z"),
            )
        }
    }

    @Test
    fun rejectsUpdatedAtBeforeCreatedAtWithinSameMillisecond() {
        assertFailsWith<IllegalArgumentException> {
            createUser(
                createdAt = Instant.parse("2026-08-21T08:00:00.000000500Z"),
                updatedAt = Instant.parse("2026-08-21T08:00:00.000000400Z"),
            )
        }
    }

    private fun createUser(
        id: UserId = UserId("firebase-user_A1b2C3"),
        displayName: String = "Rafael",
        createdAt: Instant = Instant.parse("2026-08-21T08:00:00Z"),
        updatedAt: Instant = createdAt,
    ): User {
        return User(
            id = id,
            displayName = displayName,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}